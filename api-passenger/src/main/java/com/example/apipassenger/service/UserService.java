package com.example.apipassenger.service;

import com.example.apipassenger.remote.PassengerUserServiceClient;
import com.example.internalcommon.dto.PassengerUser;
import com.example.internalcommon.dto.ResponseResult;
import com.example.internalcommon.dto.TokenResult;
import com.example.internalcommon.request.VerificationDTO;
import com.example.internalcommon.util.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {

    @Autowired
    private PassengerUserServiceClient passengerUserServiceClient;

     public ResponseResult getUserByAccessToken(String accessToken) {
         log.info("accessToken: " + accessToken);
         // 解析accessToken拿到手机号
         TokenResult tokenResult = JwtUtils.checkToken(accessToken);
         String phone = tokenResult.getPhone();
         log.info("手机号： " + phone);

         // 根据手机号查询用户
         ResponseResult userByPhone = passengerUserServiceClient.getUserByPhone(phone);

         return ResponseResult.success(userByPhone.getData());
     }
}
