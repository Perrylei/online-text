package com.example.passengeruserservice.controller;

import com.example.internalcommon.dto.ResponseResult;
import com.example.internalcommon.request.VerificationDTO;
import com.example.passengeruserservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/user")
    public ResponseResult loginOrRegister(@RequestBody VerificationDTO verificationDTO) {
        String passengerPhone = verificationDTO.getPassengerPhone();
        System.out.println("手机号： " + passengerPhone);
        return userService.loginOrRegister(passengerPhone);
    }

    @GetMapping("/user/{phone}")
    public ResponseResult getUserByPhone(@PathVariable("phone") String passwngerPhone) {
        return userService.getUserByPhone(passwngerPhone);
    }
}
