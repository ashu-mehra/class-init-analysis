package org.cia.analyzer.graph;

import org.cia.analyzer.TypeScanner;
import org.cia.metadata.Callsite;
import org.cia.metadata.ClassInfo;
import org.cia.metadata.ClassRepository;
import org.cia.metadata.MethodInfo;
import org.cia.metadata.Type;

import static org.cia.util.KnownMethods.*;

public class CGBuilder {
    private final CallGraph graph;
    private final ClassRepository repo;

    public CGBuilder(ClassRepository repo) {
        this.graph = new CallGraph();
        this.repo = repo;
    }

    boolean isEntryPointCandidate(MethodInfo minfo) {
        if (minfo.getOwner().isPublic() && (minfo.getName().equals(CLINIT.methodName()) || minfo.isPublic())) {
            String desc = minfo.getParameters();
            TypeScanner scanner = TypeScanner.create(desc);
            while (scanner.hasToken()) {
                String token = scanner.nextToken();
                if (!Type.isJdkType(token) && !Type.isBasicType(token) && !Type.isBasicArrayType(token)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public CallGraph build() {
        for (ClassInfo cinfo : repo.getAllClasses()) {
            if (!Type.isJdkType(cinfo.getName())) {
                for (MethodInfo minfo : cinfo.getMethods()) {
                    Node node = graph.addNode(minfo);
                    if (isEntryPointCandidate(minfo)) {
                        graph.addEntryPoint(node);
                    }
                    handleReferencedClasses(node);
                    for (Callsite callsite : minfo.getCallsites()) {
                        MethodInfo callee = callsite.getCallee();
                        if (Type.isJdkType(callee.getOwner().getName())) {
                            continue;
                        }
                        if (minfo.equals(callee)) {
                            // ignore recursive calls
                            continue;
                        }
                        if (callee.isResolved()) {
                            node.addEdge(callee);
                        } else {
                            // callee is not defined in its owner class - this must be a call to super method
                            handleSuperMethodCalls(node, callsite);
                        }
                        if (callsite.getType() == Callsite.CallType.VIRTUAL) {
                            handleOverriddenMethods(node, callsite);
                        }
                    }
                }
            }
        }
        return graph;
    }

    private void addEdgeForClinit(Node node, ClassInfo cinfo) {
        ClassInfo parent = cinfo.getParent();
        if (parent != null) {
            addEdgeForClinit(node, parent);
        }
        MethodInfo clinit = cinfo.findMethod(CLINIT.signature());
        if (clinit != null) {
            node.addEdge(clinit);
        }
    }

    /**
     * Add edges for clinits of the referenced classes
     * @param node
     */
    private void handleReferencedClasses(Node node) {
        for (ClassInfo cinfo: node.getMethodInfo().getReferencedClasses()) {
            if (!Type.isJdkType(cinfo.getName())) {
                addEdgeForClinit(node, cinfo);
            }
        }
    }

    public void handleOverriddenMethods(Node node, Callsite callsite) {
        assert(callsite.getType() == Callsite.CallType.VIRTUAL);
        MethodInfo callee = callsite.getCallee();
        ClassInfo calleeOwner = callee.getOwner();
        if (calleeOwner.isInterface()) {
            calleeOwner.forEachImplementor(impl -> {
                MethodInfo overriddenMethod = impl.findMethod(callee.getSignature());
                if (overriddenMethod != null) {
                    node.addEdge(overriddenMethod);
                }
            });
        } else {
            calleeOwner.forEachSubclass(sub -> {
                MethodInfo overriddenMethod = sub.findMethod(callee.getSignature());
                if (overriddenMethod != null) {
                    node.addEdge(overriddenMethod);
                }
            });
        }
    }

    private MethodInfo findSuperMethod(MethodInfo minfo, ClassInfo cinfo) {
        ClassInfo parent = cinfo.getParent();
        MethodInfo superMethod = null;
        if (parent != null) {
            superMethod = parent.findMethod(minfo.getSignature());
            if (superMethod == null) {
                findSuperMethod(minfo, parent);
            }
        }
        return superMethod;
    }

    private void handleSuperMethodCalls(Node node, Callsite callsite) {
        MethodInfo superCallee = findSuperMethod(callsite.getCallee(), callsite.getCallee().getOwner());
        if (superCallee != null) {
            node.addEdge(superCallee);
        }
    }
}
