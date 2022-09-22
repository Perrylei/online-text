package com.example.apipassenger.controller;

import com.example.apipassenger.service.TokenService;
import com.example.internalcommon.dto.ResponseResult;
import com.example.internalcommon.response.TokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenController {

    @Autowired
    private TokenService tokenService;

    @PostMapping("/token-refresh")
    public ResponseResult refreshToken(@RequestBody TokenResponse tokenResponse) {
        String refreshToken = tokenResponse.getRefreshToken();
        System.out.println("old refreshToken:" + refreshToken);
        return tokenService.refreshToken(refreshToken);
    }
}
