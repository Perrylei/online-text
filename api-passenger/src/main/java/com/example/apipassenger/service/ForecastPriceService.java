package com.example.apipassenger.service;

import com.example.apipassenger.remote.PriceServiceClient;
import com.example.internalcommon.dto.ResponseResult;
import com.example.internalcommon.request.ForecastPriceDTO;
import com.example.internalcommon.response.ForecastPriceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ForecastPriceService {

    @Autowired
    private PriceServiceClient priceServiceClient;

    /**
     * 根据出发地和目的地的经纬度来统计乘车费用
     *
     * @param depLongitude 出发地经度
     * @param depLatitude 出发地维度
     * @param destLongitude 目的地经度
     * @param desLatitude 目的地维度
     */
    public ResponseResult forecastPrice(String depLongitude, String depLatitude, String destLongitude, String desLatitude) {

        log.info("出发地的经度：" + depLongitude);
        log.info("出发地的维度：" + depLatitude);
        log.info("目的地的经度：" + destLongitude);
        log.info("目的地的维度：" + desLatitude);

        // 调用计价服务，计算价格
        ForecastPriceDTO forecastPriceDTO = new ForecastPriceDTO();
        forecastPriceDTO.setDepLongitude(depLongitude);
        forecastPriceDTO.setDepLatitude(depLatitude);
        forecastPriceDTO.setDestLongitude(destLongitude);
        forecastPriceDTO.setDesLatitude(desLatitude);
        ResponseResult<ForecastPriceResponse> responseResult = priceServiceClient.forecastPrice(forecastPriceDTO);
        double price = responseResult.getData().getPrice();

        ForecastPriceResponse priceResponse = new ForecastPriceResponse();
        priceResponse.setPrice(price);
        return ResponseResult.success(priceResponse);
    }
}
