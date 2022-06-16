package org.cia.metadata.visitor;

import org.cia.metadata.ClassInfo;
import org.cia.metadata.MethodInfo;
import org.cia.metadata.ClassRepository;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.ASM9;

public class CiaClassVisitor extends ClassVisitor {
    private final ClassRepository repo;
    private final String codeSource;
    private ClassInfo cinfo;

    public CiaClassVisitor(ClassRepository repo, String codeSource) {
        super(ASM9);
        this.repo = repo;
        this.codeSource = codeSource;
    }

    public void visit(int version, int access, java.lang.String name, java.lang.String signature, java.lang.String superName, java.lang.String[] interfaces) {
        cinfo = repo.addClass(name);
        cinfo.setCodeSource(codeSource);
        cinfo.setAccessFlags(access);
        if (superName != null) {
            ClassInfo parent = repo.addClass(superName);
            cinfo.setParent(parent);
        }
        for (String intf: interfaces) {
            ClassInfo intfInfo = repo.addClass(intf);
            cinfo.addInterface(intfInfo);
        }
        super.visit(version, access, name, signature, superName, interfaces);
    }

    public MethodVisitor visitMethod(int access, java.lang.String name, java.lang.String descriptor, java.lang.String signature, java.lang.String[] exceptions) {
        MethodInfo minfo = new MethodInfo(cinfo, name, descriptor);
        minfo.setAccessFlags(access);
        cinfo.addMethod(minfo);
        MethodVisitor delegate = super.visitMethod(access, name, descriptor, signature, exceptions);
        return new CiaMethodVisitor(delegate, repo, minfo);
    }
}
