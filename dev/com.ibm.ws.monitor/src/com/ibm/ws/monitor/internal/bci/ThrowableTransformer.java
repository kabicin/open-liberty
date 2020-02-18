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

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

public class ThrowableTransformer implements ClassFileTransformer {

    final String BASE_TRACE_SERVICE_CLASS_NAME = "com.ibm.ws.logging.internal.impl.BaseTraceService";
    final String THROWABLE_AT_ENTRY_CLASS_NAME = "com.ibm.ws.ras.instrument.annotation.ThrowableAtEntry";
    final String THROWABLE_AT_RETURN_CLASS_NAME = "com.ibm.ws.ras.instrument.annotation.ThrowableAtReturn";

    private static Instrumentation inst = null;

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) throws IllegalClassFormatException {
        byte[] ba = null;
        try {
            ba = transformClassForRequestProbe(classfileBuffer, className);
        } catch (Exception e) {

            throw e;
        }
        return ba;
    }

    private byte[] transformClassForRequestProbe(byte[] cBuffer, String nameOfClass) {
        ClassReader reader = new ClassReader(cBuffer);
        ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES);
        ClassVisitor visitor = writer;
        if (nameOfClass.equals("java/lang/Throwable")) {
            visitor = new ThrowableClassAdapter(visitor);
        }
        reader.accept(visitor, reader.SKIP_FRAMES);
        return writer.toByteArray();
    }

}
