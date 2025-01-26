package com.techstud.sch_auth.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ResponseUtil {

    public <T> ResponseEntity<T> okWithCookies(T body, ResponseCookie... cookies) {
        var response = ResponseEntity.ok();
        for (ResponseCookie cookie : cookies) {
            response = response.header(HttpHeaders.SET_COOKIE, cookie.toString());
        }
        return response.body(body);
    }

    public ResponseEntity<Void> okWithHeadersOnly(Map<String, String> headers) {
        var response = ResponseEntity.ok();
        for (Map.Entry<String, String> header : headers.entrySet()) {
            response = response.header(header.getKey(), header.getValue());
        }
        return response.build();
    }
}
