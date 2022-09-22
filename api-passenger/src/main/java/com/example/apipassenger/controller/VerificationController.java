package com.example.apipassenger.controller;

import com.example.apipassenger.service.VerificationCodeService;
import com.example.internalcommon.dto.ResponseResult;
import com.example.internalcommon.request.VerificationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VerificationController {

    @Autowired
    private VerificationCodeService verificationCodeService;

    @GetMapping("/verification-code")
    public ResponseResult verificationCode(@RequestBody VerificationDTO verificationDTO) {
        String passengerPhone = verificationDTO.getPassengerPhone();
        System.out.println("接收到的手机号码：" + passengerPhone);
        return verificationCodeService.generatorCode(passengerPhone);
    }

    @PostMapping("/verification-code-check")
    public ResponseResult checkVerificationCode(@RequestBody VerificationDTO verificationDTO) {
        String passengerPhone = verificationDTO.getPassengerPhone();
        String verificationCode = verificationDTO.getVerificationCode();
        System.out.println("手机号：" + passengerPhone + ", 验证码：" + verificationCode);
        return verificationCodeService.checkCode(passengerPhone, verificationCode);
    }
}
