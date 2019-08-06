package com.ibm.ws.microprofile.health20.test;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;


@ApplicationScoped
public class CDIProducerLivenessCheck {

    @Liveness
    @Produces
    HealthCheck databaseCheck() {
        return () -> HealthCheckResponse.named("cdi-producer-liveness-check").up().build();
    }
}
