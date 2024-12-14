package com.techstud.sch_auth.swagger;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAlreadyExistResponse {
    private String systemName;
    private String applicationName;
    private String error;
}
