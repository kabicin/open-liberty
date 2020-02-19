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

import java.io.PrintStream;
import java.lang.reflect.Method;

/**
 * The ThrowableProxy exposes annotated BaseTraceService methods to the Java boot class loader
 *
 */
public final class ThrowableProxy {

    /**
     * The method to be fired upon a stack trace being entered.
     */
    private static Method fireMethod;

    /**
     * The class object needed for the method to run.
     */
    private static Object fireTarget;

    /**
     * Sets the fire target
     *
     * @param target where the method resides
     * @param method to be fired
     * @param isEnter true if the method is being entered and false if the method is being returned
     */
    public final static void setFireTarget(Object target, Method method) {
        fireTarget = target;
        fireMethod = method;
    }

    /**
     * Invokes the fireReturnMethod from the fireTarget
     */
    public final static boolean fireMethod(Throwable t, PrintStream originalStream) {
    	Boolean b = Boolean.FALSE;
        try {
            b = (Boolean) fireMethod.invoke(fireTarget, t, originalStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

}
