package com.ibm.ws.ras.instrument.internal.main;

import java.security.ProtectionDomain;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import com.ibm.ws.ras.instrument.internal.bci.ThrowableClassAdapter;
import com.ibm.ws.ras.instrument.internal.model.ThrowableInfo;

import org.objectweb.asm.ClassVisitor;

import java.lang.annotation.Annotation;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.Method;

import java.lang.instrument.Instrumentation;


public class ThrowableTransformer implements ClassFileTransformer {
	
	final String BASE_TRACE_SERVICE_CLASS_NAME = "com.ibm.ws.logging.internal.impl.BaseTraceService";
	final String THROWABLE_AT_ENTRY_CLASS_NAME = "com.ibm.ws.ras.instrument.annotation.ThrowableAtEntry";
	final String THROWABLE_AT_RETURN_CLASS_NAME = "com.ibm.ws.ras.instrument.annotation.ThrowableAtReturn";
	
	private ThrowableInfo throwableInfo;
	
	private static Instrumentation inst = null;
	public ThrowableTransformer(ThrowableInfo throwableInfo) {
		this.throwableInfo = throwableInfo;	
	}
	
	
	
	@Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		byte[] ba = null;
		 if (className.startsWith("com/ibm/ws/boot")) {
	            System.out.println("Booting: " + className);
	        }
        try {
        	ba = transformClassForRequestProbe(classfileBuffer, className);
        } catch (Exception e) {
        	
        	throw e;
        }
        return ba;
    }
	
	 private byte[] transformClassForRequestProbe(byte[] cBuffer, String nameOfClass) {
		ClassReader reader = new ClassReader(cBuffer);
		ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES);
		ClassVisitor visitor = writer;
		if (nameOfClass.equals("java/lang/Throwable")) {
			visitor = new ThrowableClassAdapter(visitor, throwableInfo);
		}
		reader.accept(visitor, reader.SKIP_FRAMES);
		return writer.toByteArray();
     }
	 
	 
	
}
