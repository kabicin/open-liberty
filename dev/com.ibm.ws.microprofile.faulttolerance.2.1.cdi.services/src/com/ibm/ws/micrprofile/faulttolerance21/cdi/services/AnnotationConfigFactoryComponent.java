/*******************************************************************************
 * Copyright (c) 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.micrprofile.faulttolerance21.cdi.services;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

import com.ibm.ws.microprofile.faulttolerance.cdi.config.AnnotationConfigFactory;
import com.ibm.ws.microprofile.faulttolerance21.cdi.config.impl.AnnotationConfigFactoryImpl21;

@Component(service = AnnotationConfigFactory.class, configurationPolicy = ConfigurationPolicy.IGNORE)
public class AnnotationConfigFactoryComponent extends AnnotationConfigFactoryImpl21 {
}