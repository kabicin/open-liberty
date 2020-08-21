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
package com.ibm.ws.tx.jta.test;

import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import com.ibm.websphere.simplicity.ShrinkHelper;
import com.ibm.ws.tx.jta.web.SimpleServlet;

import componenttest.annotation.Server;
import componenttest.annotation.TestServlet;
import componenttest.custom.junit.runner.FATRunner;
import componenttest.custom.junit.runner.RepeatTestFilter;
import componenttest.rules.repeater.EE8FeatureReplacementAction;
import componenttest.rules.repeater.JakartaEE9Action;
import componenttest.topology.impl.LibertyServer;
import componenttest.topology.utils.FATServletClient;

/**
 * Example Shrinkwrap FAT project:
 * <li> Application packaging is done in the @BeforeClass, instead of ant scripting.
 * <li> Injects servers via @Server annotation. Annotation value corresponds to the
 * server directory name in 'publish/servers/%annotation_value%' where ports get
 * assigned to the LibertyServer instance when the 'testports.properties' does not
 * get used.
 * <li> Specifies an @RunWith(FATRunner.class) annotation. Traditionally this has been
 * added to bytecode automatically by ant.
 * <li> Uses the @TestServlet annotation to define test servlets. Notice that not all @Test
 * methods are defined in this class. All of the @Test methods are defined on the test
 * servlet referenced by the annotation, and will be run whenever this test class runs.
 */
@RunWith(FATRunner.class)
public class SimpleTest extends FATServletClient {

    public static final String APP_NAME = "txjta";
    public static final String SERVLET_NAME = "txjta/SimpleServlet";

    @Server("tx.jta_fat")
    @TestServlet(servlet = SimpleServlet.class, contextRoot = APP_NAME)
    public static LibertyServer server;

    @BeforeClass
    public static void setUp() throws Exception {
        // Create a WebArchive that will have the file name 'app1.war' once it's written to a file
        // Include the 'app1.web' package and all of it's java classes and sub-packages
        // Automatically includes resources under 'test-applications/APP_NAME/resources/' folder
        // Exports the resulting application to the ${server.config.dir}/apps/ directory
        ShrinkHelper.defaultApp(server, APP_NAME, "com.ibm.ws.tx.jta.*");

        server.installSystemFeature("txjtafat-1.2");
        server.installSystemFeature("txjtafat-2.0");

        // Use test-specific public features (e.g. txjtafat-x.y) to enable protected features
        // jta-x.y on the server. And since these public features are not in the repeatable EE
        // feature set, the following sets the appropriate features for each repeatable test.
        if (JakartaEE9Action.isActive()) {
            server.changeFeatures(Arrays.asList("txjtafat-2.0", "servlet-5.0", "componenttest-2.0", "osgiconsole-1.0"));
        } else if (EE8FeatureReplacementAction.ID.equals(RepeatTestFilter.CURRENT_REPEAT_ACTION)) { // e.g. isActive()
            server.changeFeatures(Arrays.asList("txjtafat-1.2", "servlet-4.0", "componenttest-1.0", "osgiconsole-1.0"));
        } else {
            server.changeFeatures(Arrays.asList("txjtafat-1.2", "servlet-3.1", "componenttest-1.0", "osgiconsole-1.0"));
        }

        server.startServer();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        if (server.isStarted()) {
            server.stopServer("WTRN0017W");
        }

        server.uninstallSystemFeature("txjtafat-1.2");
        server.uninstallSystemFeature("txjtafat-2.0");
    }
}
