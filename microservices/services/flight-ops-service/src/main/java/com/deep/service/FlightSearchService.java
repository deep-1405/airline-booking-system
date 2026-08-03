package com.deep.service;

import com.deep.payload.request.FlightSearchRequest;
import com.deep.payload.response.FlightInstanceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

// service/FlightSearchService.java
public interface FlightSearchService {
    Page<FlightInstanceResponse> searchFlights(FlightSearchRequest request, Pageable pageable);
}