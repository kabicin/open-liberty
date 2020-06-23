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
package com.ibm.ws.microprofile.health20.fat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.net.HttpURLConnection;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ibm.websphere.simplicity.ShrinkHelper;
import com.ibm.websphere.simplicity.log.Log;

import componenttest.annotation.Server;
import componenttest.custom.junit.runner.FATRunner;
import componenttest.topology.impl.LibertyServer;
import componenttest.topology.utils.HttpUtils;

/**
 *
 */
@RunWith(FATRunner.class)
public class FailsToStartHealthCheckTest {

    private static final String[] EXPECTED_FAILURES = { "CWWKE1102W", "CWWKE1105W", "CWMH0052W", "CWMH0053W" };

    public static final String APP_NAME = "FailsToStartHealthCheckApp";
    public static final int APP_STARTUP_TIMEOUT = 120 * 1000;

    private final String HEALTH_ENDPOINT = "/health";
    private final String READY_ENDPOINT = "/health/ready";
    private final String LIVE_ENDPOINT = "/health/live";

    private final int SUCCESS_RESPONSE_CODE = 200;
    private final int FAILED_RESPONSE_CODE = 503;

    @Server("FailsToStartHealthCheck")
    public static LibertyServer server1;

    @BeforeClass
    public static void setUp() throws Exception {
        log("setUp", "Add Fails To Start Application to the server.");
        WebArchive app = ShrinkHelper.buildDefaultApp(APP_NAME, "com.ibm.ws.microprofile.health20.fails.to.start.health.checks.app");
        ShrinkHelper.exportAppToServer(server1, app);

        log("setUp", "Start server");
        if (!server1.isStarted())
            server1.startServer();

        log("setUp", "Wait for app startup");
        server1.waitForStringInLog("CWWKZ0001I.* " + APP_NAME, APP_STARTUP_TIMEOUT);
        log("setUp", "Wait for expected app failure");
        server1.waitForStringInLog("CWWKZ0012I.* " + APP_NAME, APP_STARTUP_TIMEOUT);
        log("setUp", "Wait for expected FFDC failure");
        server1.waitForMultipleStringsInLog(3, "FFDC1015I");

//        server1.waitForStringInLog("CWWKT0016I: Web application available.*FailsToStartHealthCheckApp*");
    }

    @AfterClass
    public static void tearDown() throws Exception {
        server1.stopServer(EXPECTED_FAILURES);
    }

    @Test
    public void testSuccessLivenessCheckWithFailsToStartApplication() throws Exception {
        log("testSuccessLivenessCheckWithFailsToStartApplication", "Testing the /health/live endpoint");
        HttpURLConnection conLive = HttpUtils.getHttpConnectionWithAnyResponseCode(server1, LIVE_ENDPOINT);
        assertEquals(SUCCESS_RESPONSE_CODE, conLive.getResponseCode());

        JsonObject jsonResponse = getJSONPayload(conLive);
        JsonArray checks = (JsonArray) jsonResponse.get("checks");
        assertEquals(1, checks.size());
        assertTrue("The health check name did not exist in JSON object.", checkIfHealthCheckNameExists(checks, "FailsToStartHealthCheckApp"));
        assertEquals(jsonResponse.getString("status"), "DOWN");
    }

    @Test
    public void testSuccessReadinessCheckWithFailsToStartApplication() throws Exception {
        log("testSuccessReadinessCheckWithFailsToStartApplication", "Testing the /health/ready endpoint");
        HttpURLConnection conLive = HttpUtils.getHttpConnectionWithAnyResponseCode(server1, READY_ENDPOINT);
        assertEquals(SUCCESS_RESPONSE_CODE, conLive.getResponseCode());

        JsonObject jsonResponse = getJSONPayload(conLive);
        JsonArray checks = (JsonArray) jsonResponse.get("checks");
        assertEquals(1, checks.size());
        assertTrue("The health check name did not exist in JSON object.", checkIfHealthCheckNameExists(checks, "FailsToStartHealthCheckApp"));
        assertEquals(jsonResponse.getString("status"), "DOWN");
    }

    @Test
    public void testSuccessCheckWithFailsToStartApplication() throws Exception {
        log("testSuccessCheckWithFailsToStartApplication", "Testing the /health endpoint");
        HttpURLConnection conLive = HttpUtils.getHttpConnectionWithAnyResponseCode(server1, HEALTH_ENDPOINT);
        assertEquals(SUCCESS_RESPONSE_CODE, conLive.getResponseCode());

        JsonObject jsonResponse = getJSONPayload(conLive);
        JsonArray checks = (JsonArray) jsonResponse.get("checks");
        assertEquals(2, checks.size());
        assertTrue("The health check name did not exist in JSON object.", checkIfHealthCheckNameExists(checks, "FailsToStartHealthCheckApp"));
        assertEquals(jsonResponse.getString("status"), "DOWN");
    }

    public JsonObject getJSONPayload(HttpURLConnection con) throws Exception {
        assertEquals("application/json; charset=UTF-8", con.getHeaderField("Content-Type"));

        BufferedReader br = HttpUtils.getResponseBody(con, "UTF-8");
        Json.createReader(br);
        JsonObject jsonResponse = Json.createReader(br).readObject();
        br.close();

        log("getJSONPayload", "Response: jsonResponse= " + jsonResponse.toString());
        assertNotNull("The contents of the health endpoint must not be null.", jsonResponse.getString("status"));

        return jsonResponse;
    }

    /**
     * Returns true if the expectedName, is found within JsonArray checks.
     */
    private boolean checkIfHealthCheckNameExists(JsonArray checks, String expectedName) {
        for (int i = 0; i < checks.size(); i++) {
            if (checks.getJsonObject(i).getString("name").equals(expectedName))
                return true;
        }
        return false;
    }

    /**
     * Helper for simple logging.
     */
    private static void log(String method, String msg) {
        Log.info(FailsToStartHealthCheckTest.class, method, msg);
    }
}
