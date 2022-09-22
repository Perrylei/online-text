package com.example.apipassenger.controller;

import com.example.internalcommon.dto.ResponseResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/auth")
    public ResponseResult authTest() {
        return ResponseResult.success("auth...");
    }

    @GetMapping("/noauth")
    public ResponseResult noAuthTest() {
        return ResponseResult.success("no auth...");
    }
}
