/*******************************************************************************
 * Copyright (c) 2019, 2020 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.wsat.ejbcdi.client.cdi;

import javax.xml.ws.WebFault;

@WebFault(name = "SQLException", targetNamespace = "http://server.ejbcdi.wsat.ws.ibm.com/")
public class SQLException_Exception
    extends Exception
{

    /**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private SQLException faultInfo;

    /**
     * 
     * @param message
     * @param faultInfo
     */
    public SQLException_Exception(String message, SQLException faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param message
     * @param faultInfo
     * @param cause
     */
    public SQLException_Exception(String message, SQLException faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: com.ibm.ws.wsat.ejbcdi.client.cdi.SQLException
     */
    public SQLException getFaultInfo() {
        return faultInfo;
    }

}
