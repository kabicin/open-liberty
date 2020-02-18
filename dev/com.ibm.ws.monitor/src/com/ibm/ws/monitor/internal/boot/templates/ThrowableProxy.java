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

/**
 *
 */

import java.lang.reflect.Method;

public final class ThrowableProxy {

    private static Method fireEnterMethod;
    private static Method fireReturnMethod;
    private static Object fireTarget;

    public final static void setFireTarget(Object target, Method method, boolean isEnter) {
        fireTarget = target;
        if (isEnter)
            fireEnterMethod = method;
        else
            fireReturnMethod = method;
    }

    public final static void fireThrowableOnEnter() {
        try {
            fireEnterMethod.invoke(fireTarget);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final static void fireThrowableOnReturn() {
        try {
            fireReturnMethod.invoke(fireTarget);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
