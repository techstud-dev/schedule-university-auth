package com.techstud.sch_auth.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Embeddable
public class RefreshToken {

    private String refreshToken;
    private LocalDateTime expiryDate;

}
