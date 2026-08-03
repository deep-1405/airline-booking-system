package com.deep.client;

import com.deep.payload.response.FareResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// client/PricingClient.java
@FeignClient(name = "pricing-service", fallback = PricingClientFallback.class)
public interface PricingClient {
    @PostMapping("/api/fares/search")
    Map<Long, FareResponse> getLowestFarePerFlight(
            @RequestBody List<Long> flightIds,
            @RequestParam("cabinClassId") Long cabinClassId);

    @GetMapping("/api/fares/lowest/flight/{flightId}/cabin-class/{cabinClassId}")
    FareResponse getLowestFareForFlightAndCabinClass(
            @PathVariable Long flightId,
            @PathVariable Long cabinClassId
    );
}