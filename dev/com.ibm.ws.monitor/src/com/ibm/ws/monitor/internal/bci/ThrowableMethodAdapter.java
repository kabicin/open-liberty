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
package com.ibm.ws.monitor.internal.bci;

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

    private final boolean observedFirstInstruction = false;
    private static boolean insidePST = false;

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

    @Override
    public void visitCode() {
        if (name.equals("printStackTrace") && desc.equals("()V")) {
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(
                               INVOKESTATIC,
                               "com/ibm/ws/boot/delegated/monitoring/ThrowableProxy",
                               "fireThrowableOnMethod",
                               Type.getMethodDescriptor(
                                                        Type.VOID_TYPE,
                                                        new Type[] { Type.getType(Throwable.class) }),
                               false);
            mv.visitInsn(RETURN);
        } else if (name.equals("printStackTrace") && desc.equals("(Ljava/io/PrintStream;)V")) {
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(
                               INVOKESTATIC,
                               "com/ibm/ws/boot/delegated/monitoring/ThrowableProxy",
                               "fireThrowableOnInnerMethod",
                               Type.getMethodDescriptor(
                                                        Type.BOOLEAN_TYPE,
                                                        new Type[] { Type.getType(Throwable.class) }),
                               false);
            Label l0 = new Label();
            mv.visitJumpInsn(IFEQ, l0);
            mv.visitInsn(RETURN);
            Label l1 = new Label();
            mv.visitJumpInsn(GOTO, l1);
            mv.visitLabel(l0);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitLabel(l1);

        }

        mv.visitCode();
    }
}