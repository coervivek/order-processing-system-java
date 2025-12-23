package com.demo.oms.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OpenApiConfigTest {

    @Test
    void orderManagementAPI_Configuration() {
        OpenApiConfig config = new OpenApiConfig();
        
        OpenAPI openAPI = config.orderManagementAPI();
        
        assertNotNull(openAPI);
        assertNotNull(openAPI.getInfo());
        assertEquals("Order Management System API", openAPI.getInfo().getTitle());
        assertEquals("E-commerce Order Management System with SAGA pattern", 
                     openAPI.getInfo().getDescription());
        assertEquals("1.0", openAPI.getInfo().getVersion());
    }
}
