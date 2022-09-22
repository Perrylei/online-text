package com.example.mapservice.remote;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.internalcommon.constant.AmapConfigConstants;
import com.example.internalcommon.response.DirectionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class MapDirectionClient {

    @Value("${mmap.key}")
    private String mmapKey;

    @Autowired
    private RestTemplate restTemplate;

    public DirectionResponse direction(String depLongitude, String depLatitude, String destLongitude, String desLatitude) {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(AmapConfigConstants.DIRECTION_URL);
        urlBuilder.append("?");
        urlBuilder.append("origin=" + depLongitude + "," + depLatitude);
        urlBuilder.append("&");
        urlBuilder.append("destination=" + destLongitude + "," + desLatitude);
        urlBuilder.append("&");
        urlBuilder.append("extensions=base");
        urlBuilder.append("&");
        urlBuilder.append("output=json");
        urlBuilder.append("&");
        urlBuilder.append("key=" + mmapKey);
        log.info(urlBuilder.toString());

        // 调用高德接口
        ResponseEntity<String> directionEntity = restTemplate.getForEntity(urlBuilder.toString(), String.class);
        log.info("高德地图，路径规划，返回信息：" + directionEntity.getBody());

        // 解析接口
        String directionEntityString = directionEntity.getBody();
        DirectionResponse directionResponse = parseDirectionEntity(directionEntityString);
        return directionResponse;
    }

    private DirectionResponse parseDirectionEntity(String directionEntityString) {
        DirectionResponse directionResponse = null;

        try {
            JSONObject directionEntity = JSONObject.parseObject(directionEntityString);
            if (directionEntity.containsKey(AmapConfigConstants.STATUS)) {
                Integer status = directionEntity.getInteger(AmapConfigConstants.STATUS);
                if (status == 1 && directionEntity.containsKey(AmapConfigConstants.ROUTE)) {
                    directionResponse = new DirectionResponse();
                    JSONObject routeObject = directionEntity.getJSONObject(AmapConfigConstants.ROUTE);
                    JSONArray paths = routeObject.getJSONArray(AmapConfigConstants.PATHS);
                    JSONObject path = paths.getJSONObject(0);
                    if (path.containsKey(AmapConfigConstants.DISTANCE)) {
                        int distance = path.getInteger(AmapConfigConstants.DISTANCE);
                        directionResponse.setDistance(distance);
                    }
                    if (path.containsKey(AmapConfigConstants.DURATION)) {
                        int duration = path.getInteger(AmapConfigConstants.DURATION);
                        directionResponse.setDuration(duration);
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return directionResponse;
    }
}
