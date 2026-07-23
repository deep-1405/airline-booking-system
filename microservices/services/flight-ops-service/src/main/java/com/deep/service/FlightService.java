package com.deep.service;

import com.deep.enums.FlightStatus;
import com.deep.payload.request.FlightRequest;
import com.deep.payload.response.FlightResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface FlightService {

    FlightResponse createFlight(Long userId, FlightRequest request);
    List<FlightResponse> createFlights(Long userId, List<FlightRequest> requests);
    FlightResponse getFlightById(Long id);
    FlightResponse getFlightByNumber(String flightNumber);
    Page<FlightResponse> getFlightsByAirline(Long userId,
                                             Long departureAirportId,
                                             Long arrivalAirportId,
                                             Pageable pageable);
    FlightResponse updateFlight(Long id, FlightRequest request);
    FlightResponse changeStatus(Long id, FlightStatus status);
    void deleteFlight(Long id);

    Map<Long, FlightResponse> getFlightsByIds(List<Long> ids);
}
