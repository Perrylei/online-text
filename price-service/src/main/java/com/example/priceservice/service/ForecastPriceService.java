package com.example.priceservice.service;

import com.example.internalcommon.constant.CommonStatusEnum;
import com.example.internalcommon.dto.PriceRule;
import com.example.internalcommon.dto.ResponseResult;
import com.example.internalcommon.request.ForecastPriceDTO;
import com.example.internalcommon.response.DirectionResponse;
import com.example.internalcommon.response.ForecastPriceResponse;
import com.example.priceservice.mapper.PriceRuleMapper;
import com.example.priceservice.remote.MapServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ForecastPriceService {

    @Autowired
    private MapServiceClient mapServiceClient;

    @Autowired
    private PriceRuleMapper priceRuleMapper;

    public ResponseResult forecastPrice(String depLongitude, String depLatitude, String destLongitude, String desLatitude) {

        log.info("出发地的经度：" + depLongitude);
        log.info("出发地的维度：" + depLatitude);
        log.info("目的地的经度：" + destLongitude);
        log.info("目的地的维度：" + desLatitude);


        ForecastPriceDTO forecastPriceDTO = new ForecastPriceDTO();
        forecastPriceDTO.setDepLongitude(depLongitude);
        forecastPriceDTO.setDepLatitude(depLatitude);
        forecastPriceDTO.setDestLongitude(destLongitude);
        forecastPriceDTO.setDesLatitude(desLatitude);
        // 调用地图服务，查询距离和时长
        ResponseResult<DirectionResponse> direction = mapServiceClient.direction(forecastPriceDTO);

        Integer distance = direction.getData().getDistance();
        Integer duration = direction.getData().getDuration();
        log.info("距离：" + distance + " 时间：" + duration);

        // 读取计价规则
        Map<String, Object> map = new HashMap<>();
        map.put("city_code", "110000");
        map.put("vehicle_type", "1");
        List<PriceRule> priceRules = priceRuleMapper.selectByMap(map);

        if (priceRules.size() == 0) {
            return ResponseResult.fail(CommonStatusEnum.PRICE_RULE_ERROR.getCode(), CommonStatusEnum.PRICE_RULE_ERROR.getValue());
        }

        // 根据距离，时长和计价规则计算价格
        PriceRule priceRule = priceRules.get(0);


        ForecastPriceResponse priceResponse = new ForecastPriceResponse();
        priceResponse.setPrice(12.25);
        return ResponseResult.success(priceResponse);
    }

    /**
     * 根据距离，时长和计价规则，计算价格
     *
     * @param distance
     * @param duration
     * @param priceRule
     * @return
     */
    private double getPrice(Integer distance, Integer duration, PriceRule priceRule) {
        BigDecimal price = new BigDecimal(0);

        // 起步价
        Double startFare = priceRule.getStartFare();
        BigDecimal startFareDecimal = new BigDecimal(startFare);
        price.add(startFareDecimal);

        // 公里数
        BigDecimal distanceDecimal = new BigDecimal(distance);

        // 总里程（km）
        BigDecimal distanceMileDecimal = distanceDecimal.divide(new BigDecimal(1000), 2, BigDecimal.ROUND_HALF_UP);

        // 起步里程
        Integer startMile = priceRule.getStartMile();
        BigDecimal startMileDecimal = new BigDecimal(startMile);
        double subtractDecimal = distanceMileDecimal.subtract(startMileDecimal).doubleValue();
        double mile = subtractDecimal < 0 ? 0 : subtractDecimal;
        BigDecimal mileDecilaml = new BigDecimal(mile);
        // 计程单价
        Double unitPricePerMile = priceRule.getUnitPricePerMile();
        BigDecimal unitPricePerMileDecimal = new BigDecimal(unitPricePerMile);

        BigDecimal mileFare = mileDecilaml.multiply(unitPricePerMileDecimal).setScale(2, BigDecimal.ROUND_HALF_UP);
        price = price.add(mileFare);

        // 时长费
        BigDecimal time = new BigDecimal(duration);
        // 时长分钟数
        BigDecimal timeDecimal = time.divide(new BigDecimal(60), 2, BigDecimal.ROUND_HALF_UP);
        // 计价单价
        Double unitPricePerMinute = priceRule.getUnitPricePerMinute();
        BigDecimal unitPriceMinuteDecimal = new BigDecimal(unitPricePerMinute);
        // 时长费用
        BigDecimal timeFare = timeDecimal.multiply(unitPriceMinuteDecimal);
        price = price.add(timeFare);

        return price.doubleValue();
    }
}
