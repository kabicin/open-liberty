package com.ibm.ws.ras.instrument.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The {@code ThrowableAtEntry} annotation is used to mark a method as a
 * the target of a <em>method entry</em> event.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ThrowableAtReturn {
	String method();
}