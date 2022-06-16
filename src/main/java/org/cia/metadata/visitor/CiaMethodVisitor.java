package org.cia.metadata.visitor;

import org.cia.metadata.Callsite;
import org.cia.metadata.ClassInfo;
import org.cia.metadata.MethodInfo;
import org.cia.metadata.ClassRepository;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class CiaMethodVisitor extends MethodVisitor {
    private final ClassRepository repo;
    private final MethodInfo minfo;
    public CiaMethodVisitor(MethodVisitor delegate, ClassRepository repo, MethodInfo minfo) {
        super(ASM9, delegate);
        this.repo = repo;
        this.minfo = minfo;
    }

    private boolean isSameOwner(String owner) {
        if (owner.equals(minfo.getOwner().getName())) {
            return true;
        }
        return false;
    }

    private void addReferencedClass(String owner) {
        if (isSameOwner(owner)) {
            return;
        }
        ClassInfo clinitOwner = repo.addClass(owner);
        minfo.addReferencedClasss(clinitOwner);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        ClassInfo calleeOwner = repo.addClass(owner);
        if (opcode == INVOKESTATIC) {
            addReferencedClass(owner);
            MethodInfo callee = new MethodInfo(calleeOwner, name, descriptor);
            calleeOwner.addMethod(callee);
            minfo.addCallsite(callee, Callsite.CallType.STATIC);
        } else if (opcode == INVOKEVIRTUAL || opcode == INVOKEINTERFACE) {
            MethodInfo callee = new MethodInfo(calleeOwner, name, descriptor);
            calleeOwner.addMethod(callee);
            minfo.addCallsite(callee, Callsite.CallType.VIRTUAL);
        } else if (opcode == INVOKESPECIAL) {
            MethodInfo callee = new MethodInfo(calleeOwner, name, descriptor);
            calleeOwner.addMethod(callee);
            minfo.addCallsite(callee, Callsite.CallType.SPECIAL);
        }
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
        ClassInfo calleeOwner = repo.addClass(bootstrapMethodHandle.getOwner());
        addReferencedClass(bootstrapMethodHandle.getOwner());
        MethodInfo callee = new MethodInfo(calleeOwner, bootstrapMethodHandle.getName(), bootstrapMethodHandle.getDesc());
        minfo.addCallsite(callee, Callsite.CallType.STATIC);
        super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
    }

    @Override
    public void visitFieldInsn(int opcode, java.lang.String owner, java.lang.String name, java.lang.String descriptor) {
        if (opcode == PUTSTATIC || opcode == GETSTATIC) {
            addReferencedClass(owner);
        }
    }

    @Override
    public void visitEnd() {
        minfo.setResolved(true);
        super.visitEnd();
    }

    @Override
    public void visitTypeInsn(int opcode, java.lang.String type) {
        if (opcode ==  NEW) {
            addReferencedClass(type);
        }
    }
}
