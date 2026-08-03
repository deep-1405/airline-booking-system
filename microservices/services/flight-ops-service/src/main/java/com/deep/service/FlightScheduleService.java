package com.deep.service;

import com.deep.payload.request.FlightScheduleRequest;
import com.deep.payload.response.FlightScheduleResponse;

import java.util.List;

public interface FlightScheduleService {
    FlightScheduleResponse createFlightSchedule(Long userId, FlightScheduleRequest request) throws Exception;
    FlightScheduleResponse getFlightScheduleById(Long id) throws Exception;
    List<FlightScheduleResponse> getFlightScheduleByAirline(Long userId);
    FlightScheduleResponse updateFlightSchedule(Long id, FlightScheduleRequest request) throws Exception;
    void deleteFlightSchedule(Long id);
}