package com.ibm.ws.microprofile.health20.test;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;


@ApplicationScoped
public class CDIProducerReadinessCheck {

    @Readiness
    @Produces
    HealthCheck databaseCheck() {
        return () -> HealthCheckResponse.named("cdi-producer-readiness-check").up().build();
    }
}
