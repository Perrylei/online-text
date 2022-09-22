package com.example.apipassenger.service;

import com.example.apipassenger.remote.PassengerUserServiceClient;
import com.example.apipassenger.remote.VerificationServiceClient;
import com.example.internalcommon.constant.CommonStatusEnum;
import com.example.internalcommon.constant.IdentityConstants;
import com.example.internalcommon.constant.TokenConstants;
import com.example.internalcommon.dto.ResponseResult;
import com.example.internalcommon.request.VerificationDTO;
import com.example.internalcommon.response.NumberCodeResponse;
import com.example.internalcommon.response.TokenResponse;
import com.example.internalcommon.util.JwtUtils;
import com.example.internalcommon.util.RedisPreflixUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class VerificationCodeService {

    @Autowired
    private VerificationServiceClient verificationServiceClient;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private PassengerUserServiceClient passengerUserServiceClient;



    /**
     * 生成验证码
     *
     * @param passengerPhone
     * @return
     */
    public ResponseResult generatorCode(String passengerPhone) {
        // 调用验证码服务，获取验证码
        ResponseResult<NumberCodeResponse> numberCodeResponse = verificationServiceClient.getNumberCode(6);
        int numberCode = numberCodeResponse.getData().getNumberCode();
        // 存入redis
        String key = RedisPreflixUtils.generatorKeyByPhone(passengerPhone);
        // 存入Redis
        stringRedisTemplate.opsForValue().set(key, numberCode + "", 2, TimeUnit.MINUTES);
        // 通过短信服务商，将对应的验证码发送到手机上，阿里短信服务，腾讯短信服务，华信，容联

        return ResponseResult.success();
    }


    /**
     * 检验验证码
     *
     * @param passengerPhone   手机号
     * @param verificationCode 验证码
     * @return
     */
    public ResponseResult checkCode(String passengerPhone, String verificationCode) {

        // 根据手机号，从redis读取验证码
        System.out.println("根据手机号，从redis读取验证码");

        // 1. 生成key
        String key = RedisPreflixUtils.generatorKeyByPhone(passengerPhone);

        // 2. 根据key获取value
        String redisCode = stringRedisTemplate.opsForValue().get(key);
        System.out.println("Redis中取出的value:" + redisCode);

        // 校验验证码
        System.out.println("校验验证码");
        if (StringUtils.isBlank(redisCode)) {
            return ResponseResult.fail(CommonStatusEnum.VERIFICATION_CODE_ERROR.getCode(), CommonStatusEnum.VERIFICATION_CODE_ERROR.getValue());
        }
        if (!verificationCode.trim().equalsIgnoreCase(redisCode)) {
            return ResponseResult.fail(CommonStatusEnum.VERIFICATION_CODE_ERROR.getCode(), CommonStatusEnum.VERIFICATION_CODE_ERROR.getValue());
        }

        // 判断是否有用户，并进行对应的处理
        System.out.println("判断是否有用户，并进行对应的处理");
        VerificationDTO verificationDTO = new VerificationDTO();
        verificationDTO.setPassengerPhone(passengerPhone);
        passengerUserServiceClient.loginOrRegister(verificationDTO);

        // 颁发令牌, 不因该用魔法值，用枚举
        String accessToken = JwtUtils.generatorToken(passengerPhone, IdentityConstants.PASSENGER_IDENTITY, TokenConstants.ACCESS_TOKEN_TYPE);
        String refreshToken = JwtUtils.generatorToken(passengerPhone, IdentityConstants.PASSENGER_IDENTITY, TokenConstants.REFRESH_TOKEN_TYPE);


        // 将token存储到Redis中
        String accessTokenKey = RedisPreflixUtils.generatorKeyToken(passengerPhone, IdentityConstants.PASSENGER_IDENTITY, TokenConstants.ACCESS_TOKEN_TYPE);
        stringRedisTemplate.opsForValue().set(accessTokenKey, accessToken, 30, TimeUnit.DAYS);

        String refreshTokenKey = RedisPreflixUtils.generatorKeyToken(passengerPhone, IdentityConstants.PASSENGER_IDENTITY, TokenConstants.REFRESH_TOKEN_TYPE);
        stringRedisTemplate.opsForValue().set(refreshTokenKey, refreshToken, 31, TimeUnit.DAYS);

        // 响应
        System.out.println("颁发令牌");
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken(accessToken);
        tokenResponse.setRefreshToken(refreshToken);
        return ResponseResult.success(tokenResponse);
    }


}
