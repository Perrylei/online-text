package com.example.passengeruserservice.service;

import com.example.internalcommon.constant.CommonStatusEnum;
import com.example.internalcommon.dto.PassengerUser;
import com.example.internalcommon.dto.ResponseResult;
import com.example.passengeruserservice.mapper.PassengerUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private PassengerUserMapper passengerUserMapper;

    public ResponseResult loginOrRegister(String passengerPhone) {
        System.out.println("userService phone:" + passengerPhone);
        // 根据手机号查询用户信息
        Map<String, Object> map = new HashMap<>();
        map.put("passenger_phone", passengerPhone);
        List<PassengerUser> passengerUsers = passengerUserMapper.selectByMap(map);
        System.out.println(passengerUsers.size() == 0 ? "无记录" : passengerUsers.get(0).getPassengerPhone());
        // 判断用户信息是否存在

        if (passengerUsers.size() == 0) {
            PassengerUser passengerUser = new PassengerUser();
            passengerUser.setPassengerName("张三");
            passengerUser.setPassengerGender((byte) 0);
            passengerUser.setPassengerPhone(passengerPhone);
            passengerUser.setState((byte) 0);
            LocalDateTime now = LocalDateTime.now();
            passengerUser.setGmtCreate(now);
            passengerUser.setGmtModified(now);
            // 如果不存在，插入用户信息
            passengerUserMapper.insert(passengerUser);
        }


        return ResponseResult.success();
    }

    /**
     * 根据手机号查询用户信息
     *
     * @param passwngerPhone
     * @return
     */
    public ResponseResult getUserByPhone(String passwngerPhone) {
        Map<String, Object> map = new HashMap<>();
        map.put("passenger_phone", passwngerPhone);
        List<PassengerUser> passengerUsers = passengerUserMapper.selectByMap(map);
        if (passengerUsers.size() == 0) {
            return ResponseResult.fail(CommonStatusEnum.USER_FOUND_EXISTS.getCode(), CommonStatusEnum.USER_FOUND_EXISTS.getValue());
        } else {
            PassengerUser user = passengerUsers.get(0);
            return ResponseResult.success(user);
        }
    }
}
