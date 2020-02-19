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
package com.ibm.ws.monitor.internal;

import java.lang.annotation.Annotation;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;

public class ThrowableInfo {

    final String BASE_TRACE_SERVICE_CLASS_NAME = "com.ibm.ws.logging.internal.impl.BaseTraceService";
    final String THROWABLE_AT_METHOD_CLASS_NAME = "com.ibm.ws.logging.annotation.ThrowableAtMethod";
    final String THROWABLE_AT_METHOD_INNER_CLASS_NAME = "com.ibm.ws.logging.annotation.ThrowableAtMethodInner";

    private final Instrumentation inst;
    private Method methodThrow;
    private Method methodInnerThrow;
    private Object btsInstance;

    public ThrowableInfo(Instrumentation inst) {
        this.inst = inst;
        initializeThrowableWrapperMethods();
    }

    private void initializeThrowableWrapperMethods() {
        Class<?> btsClass = retrieveClass(BASE_TRACE_SERVICE_CLASS_NAME);
        Method[] methods = btsClass.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            Annotation[] annotations = method.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation.annotationType().getName().equals(THROWABLE_AT_METHOD_CLASS_NAME)) {
                    setMethodThrow(method);
                } else if (annotation.annotationType().getName().equals(THROWABLE_AT_METHOD_INNER_CLASS_NAME)) {
                    setMethodInnerThrow(method);
                }
            }
        }
        try {
            setBtsInstance(btsClass.newInstance());
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private Class<?> retrieveClass(String classGroup) {
        if (inst != null) {
            Class[] loadedClasses = inst.getAllLoadedClasses();
            for (int i = 0; i < loadedClasses.length; i++) {
                String name = loadedClasses[i].getName();
                if (name.equals(classGroup)) {
                    return loadedClasses[i];
                }
            }
        }
        return null;
    }

    private void setMethodThrow(Method method) {
        methodThrow = method;
    }

    public Method getMethodThrow() {
        return methodThrow;
    }

    private void setMethodInnerThrow(Method method) {
        methodInnerThrow = method;
    }

    public Method getMethodInnerThrow() {
        return methodInnerThrow;
    }

    private void setBtsInstance(Object instance) {
        btsInstance = instance;
    }

    public Object getBtsInstance() {
        return btsInstance;
    }
}