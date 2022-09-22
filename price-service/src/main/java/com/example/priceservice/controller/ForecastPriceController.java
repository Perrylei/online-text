package com.example.priceservice.controller;

import com.example.internalcommon.dto.ResponseResult;
import com.example.internalcommon.request.ForecastPriceDTO;
import com.example.priceservice.service.ForecastPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ForecastPriceController {

    @Autowired
    private ForecastPriceService forecastPriceService;

    @PostMapping("/forecast-price")
    public ResponseResult forecastPrice(@RequestBody ForecastPriceDTO forecastPriceDTO) {
        String depLongitude = forecastPriceDTO.getDepLongitude();
        String depLatitude = forecastPriceDTO.getDepLatitude();
        String destLongitude = forecastPriceDTO.getDestLongitude();
        String desLatitude = forecastPriceDTO.getDesLatitude();

        return forecastPriceService.forecastPrice(depLongitude, depLatitude, destLongitude, desLatitude);
    }
}
