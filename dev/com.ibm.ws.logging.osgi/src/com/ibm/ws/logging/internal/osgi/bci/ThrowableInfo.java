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
package com.ibm.ws.logging.internal.osgi.bci;

import java.lang.annotation.Annotation;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;

public class ThrowableInfo {

    final String BASE_TRACE_SERVICE_CLASS_NAME = "com.ibm.ws.logging.internal.impl.BaseTraceService";
    final String THROWABLE_AT_METHOD_CLASS_NAME = "com.ibm.ws.logging.annotation.ThrowableAtMethod";

    private final Instrumentation inst;
    private Method btsMethod;
    private Object btsInstance;

    public ThrowableInfo(Instrumentation inst) {
        this.inst = inst;
        initializeThrowableWrapperMethods();
    }

    private void initializeThrowableWrapperMethods() {
        Class<?> btsClass = retrieveClass(BASE_TRACE_SERVICE_CLASS_NAME);
        if (btsClass != null) {
	        Method[] methods = btsClass.getDeclaredMethods();
	        for (int i = 0; i < methods.length; i++) {
	            Method method = methods[i];
	            Annotation[] annotations = method.getDeclaredAnnotations();
	            for (Annotation annotation : annotations) {
	                if (annotation.annotationType().getName().equals(THROWABLE_AT_METHOD_CLASS_NAME)) {
	                	System.out.println("Loaded BTS METHOD");
	                    setBtsMethod(method);
	                } 
	            }
	        }
	        try {
	        	System.out.println("Setting BTS instance");
	            setBtsInstance(btsClass.newInstance());
	        } catch (InstantiationException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        } catch (IllegalAccessException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
        }
        else {
        	System.err.println("Unable to get overriden printStackTrace method from BaseTraceService");
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

    private void setBtsMethod(Method method) {
        btsMethod = method;
    }

    public Method getBtsMethod() {
        return btsMethod;
    }

    private void setBtsInstance(Object instance) {
        btsInstance = instance;
    }

    public Object getBtsInstance() {
        return btsInstance;
    }
}