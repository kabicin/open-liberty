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
package com.ibm.ws.monitor.internal.boot.templates;

import java.lang.reflect.Method;

/**
 * The ThrowableProxy exposes annotated BaseTraceService methods to the bootstrap class loader.
 *
 */
public final class ThrowableProxy {

    /**
     * The method to be fired upon a stack trace being entered.
     */
    private static Method fireEnterMethod;

    /**
     * The method to be fired upon a stack trace returning.
     */
    private static Method fireReturnMethod;

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
    public final static void setFireTarget(Object target, Method method, boolean isEnter) {
        fireTarget = target;
        if (isEnter)
            fireEnterMethod = method;
        else
            fireReturnMethod = method;
    }

    /**
     * Invokes the fireEnterMethod from the fireTarget
     */
    public final static void fireThrowableOnEnter() {
        try {
            fireEnterMethod.invoke(fireTarget);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Invokes the fireReturnMethod from the fireTarget
     */
    public final static void fireThrowableOnReturn() {
        try {
            fireReturnMethod.invoke(fireTarget);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}