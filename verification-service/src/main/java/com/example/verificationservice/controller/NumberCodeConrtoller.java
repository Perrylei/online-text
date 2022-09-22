package com.example.verificationservice.controller;

import com.example.internalcommon.dto.ResponseResult;
import com.example.internalcommon.response.NumberCodeResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NumberCodeConrtoller {

    @GetMapping("/memberCode/{size}")
    public ResponseResult numberCode(@PathVariable("size") int size){
        double random = (Math.random() * 9 + 1) * Math.pow(10, size - 1);
        int resultInt = (int) random;
        NumberCodeResponse response = new NumberCodeResponse();
        response.setNumberCode(resultInt);
        return ResponseResult.success(response);
    }
}
