package com.deep.client;

import com.deep.payload.response.AirportResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "location-service", fallback = LocationClientFallback.class)
public interface LocationClient {

    @GetMapping("/api/airport/{id}")
    AirportResponse getAirportById(@PathVariable Long id);
}
