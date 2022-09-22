package com.example.internalcommon.util;

public class RedisPreflixUtils {

    // 乘客验证码的前缀
    public static String verificationCodePreflix = "passenger-verification-code-";

    // 乘客token的qianzhui
    public static String tokenPreflix = "token-";

    /**
     * 根据手机号生成key
     *
     * @param passengerPhone
     * @return
     */
    public static String generatorKeyByPhone(String passengerPhone) {
        return verificationCodePreflix + passengerPhone;
    }

    /**
     * 根据手机号和身份标识生成token在redis中存储的key
     *
     * @param passengerPhone
     * @param identity
     * @return
     */
    public static String generatorKeyToken(String passengerPhone, String identity, String tokenType) {
        return tokenPreflix + passengerPhone + "-" + identity + "-" + tokenType;
    }

}
