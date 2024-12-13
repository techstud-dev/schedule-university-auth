package com.techstud.sch_auth.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public class RefreshToken {

    private String refreshToken;
    private Long expiryDate;

}
