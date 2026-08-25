package com.fiap.api_notificacao.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "notificacao.email")
public class EmailProperties {
    
    private String from = "noreply@hospital-system.com";

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}