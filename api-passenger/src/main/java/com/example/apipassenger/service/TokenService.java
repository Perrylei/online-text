package com.example.apipassenger.service;

import com.example.internalcommon.constant.CommonStatusEnum;
import com.example.internalcommon.constant.IdentityConstants;
import com.example.internalcommon.constant.TokenConstants;
import com.example.internalcommon.dto.ResponseResult;
import com.example.internalcommon.dto.TokenResult;
import com.example.internalcommon.response.TokenResponse;
import com.example.internalcommon.util.JwtUtils;
import com.example.internalcommon.util.RedisPreflixUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TokenService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public ResponseResult refreshToken(String refreshTokenSrc) {
        // 解析refreshToken
        TokenResult tokenResult = JwtUtils.checkToken(refreshTokenSrc);
        if (tokenResult == null) {
            return ResponseResult.fail(CommonStatusEnum.TOKEN_ERROR.getCode(), CommonStatusEnum.TOKEN_ERROR.getValue());
        }
        String passengerPhone = tokenResult.getPhone();
        String identity = tokenResult.getIdentity();

        // 读取redis中的refreshToken
        String refreshTokenKey = RedisPreflixUtils.generatorKeyToken(passengerPhone, identity, TokenConstants.REFRESH_TOKEN_TYPE);
        String redisRefreshToken = stringRedisTemplate.opsForValue().get(refreshTokenKey);

        // 校验refreshToken
        if (StringUtils.isBlank(redisRefreshToken) || (!redisRefreshToken.trim().equals(refreshTokenSrc.trim()))) {
            return ResponseResult.fail(CommonStatusEnum.TOKEN_ERROR.getCode(), CommonStatusEnum.TOKEN_ERROR.getValue());
        }

        // 生成双token
        String accessToken = JwtUtils.generatorToken(passengerPhone, IdentityConstants.PASSENGER_IDENTITY, TokenConstants.ACCESS_TOKEN_TYPE);
        String refreshToken = JwtUtils.generatorToken(passengerPhone, IdentityConstants.PASSENGER_IDENTITY, TokenConstants.REFRESH_TOKEN_TYPE);


        // 将token存储到Redis中
        String accessTokenKey = RedisPreflixUtils.generatorKeyToken(passengerPhone, IdentityConstants.PASSENGER_IDENTITY, TokenConstants.ACCESS_TOKEN_TYPE);
        stringRedisTemplate.opsForValue().set(accessTokenKey, accessToken, 30, TimeUnit.DAYS);
        stringRedisTemplate.opsForValue().set(refreshTokenKey, refreshToken, 31, TimeUnit.DAYS);

        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken(accessToken);
        tokenResponse.setRefreshToken(refreshToken);
        return ResponseResult.success(tokenResponse);
    }
}
