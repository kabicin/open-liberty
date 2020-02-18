package com.ibm.ws.ras.instrument.internal.main;

import java.lang.annotation.Annotation;
import java.lang.instrument.Instrumentation;
import java.util.Collection;

import com.ibm.ws.ras.instrument.internal.model.ThrowableInfo;
//import org.osgi.service.component.ComponentContext;



public class InstrumentManagerImpl {
//	
//	Instrumentation instrumentation = null;
//
//	ThrowableTransformer transformer = null;
//	
//	ThrowableInfo throwableInfo = null;
//	
//	ComponentContext componentContext = null;
//
//    /**
//     * Reference to the component that manages the runtime availability
//     * and visibility of the bootstrap proxy implementations.
//     */
//    InstrumentProxyActivator proxyActivator = null;
//    
//	/**
//     * Activation callback from the Declarative Services runtime where the
//     * component is ready for activation.
//     *
//     * @param bundleContext the bundleContext
//     */
//    synchronized void activate(ComponentContext componentContext) throws Exception {
//        this.componentContext = componentContext;
//        
//        this.throwableInfo = new ThrowableInfo(this.instrumentation);
//        this.transformer = new ThrowableTransformer(this.throwableInfo);
//        this.proxyActivator = new InstrumentProxyActivator(componentContext.getBundleContext(), this.throwableInfo, this.instrumentation);
//        this.proxyActivator.activate();
//        this.instrumentation.addTransformer(this.transformer, true);
//    }
//    
//    
//    
//    /**
//     * Deactivation callback from the Declarative Services runtime where the
//     * component is deactivated.
//     *
//     * @param bundleContext the bundleContext
//     */
//    synchronized void deactivate() throws Exception {
//        this.proxyActivator.deactivate();
//        this.instrumentation.removeTransformer(this.transformer);
//    }
//    
//    /**
//     * Inject reference to the {@link java.lang.instrument.Instrumentation} implementation.
//     *
//     * @param instrumentationAgent the JVM's {@code Instrumentation) reference
//     */
//    protected void setInstrumentation(Instrumentation instrumentation) {
//        this.instrumentation = instrumentation;
//    }
}

