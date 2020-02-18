package com.ibm.ws.ras.instrument.internal.model;

import java.lang.annotation.Annotation;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;

public class ThrowableInfo {
	
	final String BASE_TRACE_SERVICE_CLASS_NAME = "com.ibm.ws.logging.internal.impl.BaseTraceService";
	final String THROWABLE_AT_ENTRY_CLASS_NAME = "com.ibm.ws.ras.instrument.annotation.ThrowableAtEntry";
	final String THROWABLE_AT_RETURN_CLASS_NAME = "com.ibm.ws.ras.instrument.annotation.ThrowableAtReturn";
	
	private Instrumentation inst;
	private Method preThrow;
	private Method postThrow;
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
			 for(Annotation annotation : annotations) {
				 if(annotation.annotationType().getName().equals(THROWABLE_AT_ENTRY_CLASS_NAME)) {
					 setPreThrow(method);
				 }
				 else if (annotation.annotationType().getName().equals(THROWABLE_AT_RETURN_CLASS_NAME)) {
					 setPostThrow(method);
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
	 

	 private void setPreThrow(Method method) {
		 preThrow = method;
	 }
	 
	 public Method getPreThrow() {
		 return preThrow;
	 }
	 
	 private void setPostThrow(Method method) {
		 postThrow = method;
	 }
	 
	 public Method getPostThrow() {
		 return postThrow;
	 }
	 
	 private void setBtsInstance(Object instance) {
		 btsInstance = instance;
	 }
	 
	 public Object getBtsInstance() {
		 return btsInstance;
	 }
}
