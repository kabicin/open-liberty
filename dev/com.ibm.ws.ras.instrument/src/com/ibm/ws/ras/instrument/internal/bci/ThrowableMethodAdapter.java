/*******************************************************************************
 * Copyright (c) 2020 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.ras.instrument.internal.bci;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * Applies BCI to the printStackTrace method of the java.lang.Throwable class
 * upon entering and returning from the method.
 *
 */
class ThrowableMethodAdapter extends MethodVisitor implements Opcodes {
    private String currentClass = null;
    private final String name;
    private final String desc;
    private final String signature;

    private boolean observedFirstInstruction = false;
    private boolean insidePST = false;

    public ThrowableMethodAdapter(MethodVisitor mv, String name, String desc, String signature, String className) {
        super(ASM7, mv);
        this.name = name;
        this.desc = desc;
        this.signature = signature;
        this.currentClass = className;
    }

    protected boolean isConstructor() {
        return "<init>".equals(this.name);
    }

    private void fireFirstInstruction() {
        if (!observedFirstInstruction) {
            observedFirstInstruction = true;
            if (isConstructor()) {
                onFirstInstruction();
            } else {
                onMethodEntry();
            }
        }
    }

    public void onFirstInstruction() {}

    /**
     * Invokes the fireThrowableOnEnter method from the ThrowableProxy class
     */
    public void onMethodEntry() {
        if (name.equals("printStackTrace") && !insidePST) {
            insidePST = true;
            mv.visitMethodInsn(
                               INVOKESTATIC,
                               "com/ibm/ws/boot/delegated/monitoring/ThrowableProxy",
                               "fireThrowableOnEnter",
                               Type.getMethodDescriptor(
                                                        Type.VOID_TYPE,
                                                        new Type[] {}),
                               false);
        }

    }

    /**
     * Invokes the fireThrowableOnReturn method from the ThrowableProxy class
     *
     * @param opcode
     */
    public void onMethodExit(int opcode) {
        if (name.equals("printStackTrace")) {
            mv.visitMethodInsn(
                               INVOKESTATIC,
                               "com/ibm/ws/boot/delegated/monitoring/ThrowableProxy",
                               "fireThrowableOnReturn",
                               Type.getMethodDescriptor(
                                                        Type.VOID_TYPE,
                                                        new Type[] {}),
                               false);
            insidePST = false;
        }
    }

    @Override
    public void visitCode() {
        fireFirstInstruction();
        mv.visitCode();
    }

    @Override
    public void visitEnd() {
        fireFirstInstruction();
        mv.visitEnd();
    }

    @Override
    public void visitLdcInsn(Object cst) {
        fireFirstInstruction();
        mv.visitLdcInsn(cst);
    }

    @Override
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
        fireFirstInstruction();
        mv.visitLocalVariable(name, desc, signature, start, end, index);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        fireFirstInstruction();
        return mv.visitAnnotation(desc, visible);
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        fireFirstInstruction();
        mv.visitFieldInsn(opcode, owner, name, desc);
    }

    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        fireFirstInstruction();
        mv.visitTryCatchBlock(start, end, handler, type);
    }

    @Override
    public void visitInsn(int opcode) {
        fireFirstInstruction();
        switch (opcode) {
            case RETURN:
            case ARETURN:
            case DRETURN:
            case FRETURN:
            case IRETURN:
            case LRETURN:
            case ATHROW:
                onMethodExit(opcode);
                break;
            default:
                break;
        }

        mv.visitInsn(opcode);
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        fireFirstInstruction();
        mv.visitMaxs(maxStack, maxLocals);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        fireFirstInstruction();
        mv.visitMethodInsn(opcode, owner, name, desc, itf);
    }

} 