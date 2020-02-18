package com.ibm.ws.ras.instrument.internal.bci;

import java.lang.reflect.Method;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;




class ThrowableMethodAdapter extends MethodVisitor implements Opcodes {
	private String currentClass = null;
	private String name;
	private String desc;
	private String signature;
	private Method preThrow;
	private Method postThrow;
	private Object btsInstance;
	
	private boolean observedFirstInstruction = false;
	private boolean insidePST = false;
    public ThrowableMethodAdapter(MethodVisitor mv, String name, String desc, String signature, Method preThrow, Method postThrow, Object btsInstance, String className) { 
    	super(ASM7, mv);
    	this.name = name;
    	this.desc = desc;
    	this.signature = signature;
    	this.preThrow = preThrow;
    	this.postThrow = postThrow;
    	this.btsInstance = btsInstance;
    	this.currentClass = className;
    }
    
    protected boolean isConstructor() {
        return "<init>".equals(this.name);
    }
    
    private void fireFirstInstruction() {
        if (!observedFirstInstruction) {
            observedFirstInstruction = true;
            if (isConstructor()) {
                onFirstInstruction();
            } else {
                onMethodEntry();
            }
        }
    }
    
    public void onFirstInstruction() {}
    
    private void setUpThrowableProxy() {
    	try {
//    		activateProbeProxyTarget();
    	}
    	catch (Exception e ) {
    		e.printStackTrace();
    	}
    }
    
   
//   private Method findProbeProxySetFireProbeTargetMethod() throws Exception {
////       Class<?> proxyClass = Class.forName("com.ibm.ws.ras.instrument.internal.boot.templates.ThrowableProxy");
//	   Class<?> proxyClass = Class.forName("com.ibm.ws.monitor.internal.boot.templates.ThrowableProxy");
//       Method setFireProbeTargetMethod = null;
//       try {
//    	   setFireProbeTargetMethod = proxyClass.getDeclaredMethod("setFireTarget", Object.class, Method.class);
//    	   
//       }
//       catch (Exception e) {
//    	   e.printStackTrace();
//       }
//       return setFireProbeTargetMethod;
//   }

//   private void activateProbeProxyTarget() throws Exception {
////       Class<?> clazz = Class.forName("com.ibm.ws.ras.instrument.internal.boot.templates.ThrowableProxy");
//	   Class<?> clazz = Class.forName("com.ibm.ws.monitor.internal.boot.templates.ThrowableProxy");
//       Method method = null;
//       try {
//           method = clazz.getDeclaredMethod("fireThrowableOnEnter");
//       } catch (NoSuchMethodException e) {
//    	   e.printStackTrace();
//       }
//       
//       findProbeProxySetFireProbeTargetMethod().invoke(null, clazz, method);
//   }
    
    public void onMethodEntry() {
    	if (name.equals("printStackTrace")) {
    		insidePST = true;
//    		setUpThrowableProxy();
    		mv.visitMethodInsn(
                    INVOKESTATIC,
                    "com/ibm/ws/boot/delegated/monitoring/ThrowableProxy",
                    "fireThrowableOnEnter",
                    Type.getMethodDescriptor(
                            Type.VOID_TYPE,
                            new Type[] {}), false);
//    		try {
////				preThrow.invoke(btsInstance);
//			} catch (IllegalAccessException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IllegalArgumentException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (InvocationTargetException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
    	}
    	
    }
    
    public void onMethodExit(int opcode) {
    	if (name.equals("printStackTrace")) {
//    		try {
////				postThrow.invoke(btsInstance);
//			} catch (IllegalAccessException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IllegalArgumentException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (InvocationTargetException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
    		insidePST = false;
    	}
    }

    @Override
    public void visitCode() {
    	fireFirstInstruction();
    	if (insidePST) {
        	System.out.println("[VisitCode] [" + (currentClass != null ? currentClass : "null" ) + "] [" + name + "] [" + desc + "] [" + signature + "]");
    	}
    	mv.visitCode();	
    }
    
    @Override
    public void visitEnd() {
    	fireFirstInstruction();
    	
    	if (insidePST) {
    		System.out.println("[VisitEnd] [" + (currentClass != null ? currentClass : "null" ) + "]");
    	}
    	
    	mv.visitEnd();
    }
    
    @Override
    public void visitLdcInsn(Object cst) {
    	fireFirstInstruction();
    	mv.visitLdcInsn(cst);
    }
    
    @Override
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index)
    {
    	fireFirstInstruction();
    	if (insidePST)
    		System.out.println("[LocalVariable] [" + (currentClass != null ? currentClass : "null" ) + "] ["+ name + "] [" + desc + "]" + " [" + signature + "]");
    	mv.visitLocalVariable(name, desc, signature, start, end, index);
    }
    
    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
    	fireFirstInstruction();
    	if (insidePST)
    		System.out.println("[Annotation] [" + (currentClass != null ? currentClass : "null" ) + "] ["+ desc + "] [" + String.valueOf(visible) + "]");
    	return mv.visitAnnotation(desc, visible);
//    	return null;
    }
    
    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
    	fireFirstInstruction();
    	if (insidePST)
    		System.out.println("[Field] [" + (currentClass != null ? currentClass : "null" ) + "] [" + String.valueOf(opcode) + "] [" + owner + "] [" + name + "] [" + desc + "]");
    	mv.visitFieldInsn(opcode, owner, name, desc);
    }
    
    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
    	fireFirstInstruction();
    	if (insidePST)
    		System.out.println("[TryCatchBlock] [" + (currentClass != null ? currentClass : "null" ) + "] ["+ type + "]");
    	mv.visitTryCatchBlock(start, end, handler, type);
    }
    
    @Override
    public void visitInsn(int opcode) {
    	fireFirstInstruction();
    	switch (opcode) {
            case RETURN:
            case ARETURN:
            case DRETURN:
            case FRETURN:
            case IRETURN:
            case LRETURN:
            case ATHROW:
                onMethodExit(opcode);
                break;
            default:
                break;
    	}

    mv.visitInsn(opcode);
    }
    
    @Override 
    public void visitMaxs(int maxStack, int maxLocals) {
    	fireFirstInstruction();
    	mv.visitMaxs(maxStack, maxLocals);
    }
    
	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
		fireFirstInstruction();
		if(insidePST) {
			System.out.println("[MethodInsn] [" + (currentClass != null ? currentClass : "null")+ "] [" + String.valueOf(opcode )+  "] [" + owner+  "] [" + name + "] [" + desc + "] [" + String.valueOf(itf) + "]");
		}
		mv.visitMethodInsn(opcode, owner, name, desc, itf);	
	}
	
	
}