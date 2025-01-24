package com.techstud.sch_auth.dto;

import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@Data
public class ServiceDto {

    private final String requestId;
    private final String name;

    public ServiceDto(String name) {
        this.requestId = UUID.randomUUID().toString();
        this.name = name;
    }
}
