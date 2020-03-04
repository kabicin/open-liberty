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

import com.ibm.websphere.ras.TruncatableThrowable;
import com.ibm.ws.logging.internal.PackageProcessor;

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
                if ((trace = filterStackTraces(rawStackTrace[i])) != null) {
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

    /**
     * Trim stack traces. This isn't as sophisticated as what TruncatableThrowable
     * does, since pass through all code except code which is clearly IBM-internal.
     * This means we pass through Java API and third-party calls - this means we
     * include more frames than we'd like to, but the alternative is to try and buffer
     * the whole stack so we can make passes in two directions (as we do in TruncatableThrowable).
     * However, the buffering seems terribly risky. The other alternative is to stop as soon
     * as we hit the first IBM code, but this means we lose user code in the
     * container-user-container case.
     *
     * Thread-safety is provided by a thread local variable which is used to store state.
     *
     * This filtering also won't trim stack traces which have been converted
     * into a string and then output (rather than coming out line by line).
     *
     * Once we hit something on stderr that's not a stack frame, we reset all our state.
     *
     * @param txt a line of stack trace
     * @return null if the stack trace should be suppressed, or an indicator we're suppressing,
     *         or maybe the original stack trace
     */
    public static String filterStackTraces(String txt) {
        // Check for stack traces, which we may want to trim
        StackTraceFlags stackTraceFlags = traceFlags.get();
        // We have a little thread-local state machine here with four states controlled by two
        // booleans. Our triggers are { "unknown/user code", "just seen IBM code", "second line of IBM code", ">second line of IBM code"}
        // "unknown/user code" -> stackTraceFlags.isSuppressingTraces -> false, stackTraceFlags.needsToOutputInternalPackageMarker -> false
        // "just seen IBM code" -> stackTraceFlags.needsToOutputInternalPackageMarker->true
        // "second line of IBM code" -> stackTraceFlags.needsToOutputInternalPackageMarker->true
        // ">second line of IBM code" -> stackTraceFlags.isSuppressingTraces->true
        // The final two states are optional

        if (txt.startsWith("\tat ")) {
            // This is a stack trace, do a more detailed analysis
            PackageProcessor packageProcessor = null; // PackageProcessor.getPackageProcessor();
            String packageName = ""; // PackageProcessor.extractPackageFromStackTraceLine(txt);
            // If we don't have a package processor, don't suppress anything
            if (packageProcessor != null) { // && packageProcessor.isIBMPackage(packageName)) {
                // First internal package, we let through
                // Second one, we suppress but say we did
                // If we're still suppressing, and this is a stack trace, this is easy - we suppress
                if (stackTraceFlags.isSuppressingTraces) {
                    txt = null;
                } else if (stackTraceFlags.needsToOutputInternalPackageMarker) {
                    // Replace the stack trace with something saying we got rid of it
                    txt = "\tat " + TruncatableThrowable.INTERNAL_CLASSES_STRING;
                    // No need to output another marker, we've just output it
                    stackTraceFlags.needsToOutputInternalPackageMarker = false;
                    // Suppress any subsequent IBM frames
                    stackTraceFlags.isSuppressingTraces = true;
                } else {
                    // Let the text through, but make a note not to let anything but an [internal classes] through
                    stackTraceFlags.needsToOutputInternalPackageMarker = true;
                }
            } else {
                // This is user code, third party API, or Java API, so let it through
                // Reset the flags to ensure it gets let through
                stackTraceFlags.isSuppressingTraces = false;
                stackTraceFlags.needsToOutputInternalPackageMarker = false;
            }

        } else {
            // We're no longer processing a stack, so reset all our state
            stackTraceFlags.isSuppressingTraces = false;
            stackTraceFlags.needsToOutputInternalPackageMarker = false;
        }
        return txt;
    }
}

