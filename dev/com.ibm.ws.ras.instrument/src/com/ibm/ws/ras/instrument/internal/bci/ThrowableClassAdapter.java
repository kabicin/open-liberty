package com.ibm.ws.ras.instrument.internal.bci;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.ibm.ws.ras.instrument.internal.model.ThrowableInfo;



public class ThrowableClassAdapter extends ClassVisitor implements Opcodes {

	public static String currentClass = null;
	private ThrowableInfo throwableInfo;

	public ThrowableClassAdapter(ClassVisitor cv, ThrowableInfo throwableInfo) {
        super(ASM7, cv);
        this.throwableInfo = throwableInfo;
      }

	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		cv.visit(version, access, name, signature, superName, interfaces);
		currentClass = name;
	}
	
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
    	MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);    	
    	MethodVisitor tmv = new ThrowableMethodAdapter(mv, name, desc, signature, throwableInfo.getPreThrow(), throwableInfo.getPostThrow(), throwableInfo.getBtsInstance(), currentClass);
    	return tmv != null ? tmv : mv;
    };

}
