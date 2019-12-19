package com.enterprise.luisferreira.webservices;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;

import javax.ws.rs.core.Application;

@OpenAPIDefinition(

        info = @Info(
                title = "Calls Web Service",
                description = "The goal of the following service is to manage a specific resource: Calls",
                version = "1.0.0"
        ))
public class CallsServiceApplication extends Application {
}
