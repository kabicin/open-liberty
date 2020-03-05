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
package com.ibm.ws.logging.internal.osgi.boot.templates;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * The ThrowableProxy exposes annotated BaseTraceService methods to the bootstrap class loader.
 *
 */
public final class ThrowableProxy {
    private static class StackTraceFlags {
        boolean needsToOutputInternalPackageMarker = false;
        boolean isSuppressingTraces = false;
    }

    private static final ThreadLocal<Boolean> isPrintingStackTrace = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return Boolean.FALSE;
        }
    };

    private static final ThreadLocal<StackTraceFlags> traceFlags = new ThreadLocal<StackTraceFlags>() {
        @Override
        protected StackTraceFlags initialValue() {
            return new StackTraceFlags();
        }
    };

    public static boolean printStackTraceOverride(Throwable t, PrintStream originalStream) {
        if ((originalStream == System.err || originalStream == System.out) && !isPrintingStackTrace.get()) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            isPrintingStackTrace.set(true);
            t.printStackTrace(ps);
            isPrintingStackTrace.set(false);
            String[] rawStackTrace = baos.toString().split("\\r?\\n");
            StringBuilder filteredStackTrace = new StringBuilder();
            String trace;
            for (int i = 0; i < rawStackTrace.length; i++) {
                if ((trace = rawStackTrace[i]) != null) {
                    filteredStackTrace.append(trace + "\n");
                }
            }
            if (filteredStackTrace.length() > 0) {
                originalStream.println(filteredStackTrace.toString());
            }
            return true;
        }
        return false;
    }

}