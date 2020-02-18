package com.ibm.ws.ras.instrument.internal.boot.templates;

import java.lang.reflect.Method;

/**
 * Method Proxy class for wrapping printStackTrace calls to be activated at runtime.
 */
public class MethodProxy {
	
	private static Method enterMethod;
	private static Method returnMethod;
	private static Object target;

	public final static void setTarget(Object t) {
		target = t;
	}
	
	public final static void setEnterMethod(Method method) {
	    enterMethod = method;
	}
	
	public final static void setReturnMethod(Method method) {
		returnMethod = method;
	}

	public final static void fireEnterMethod() {
	    try {
	        enterMethod.invoke(target);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public final static void fireReturnMethod() {
	    try {
	        returnMethod.invoke(target);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
}
