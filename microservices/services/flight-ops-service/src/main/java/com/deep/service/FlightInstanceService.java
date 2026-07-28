package com.deep.service;

import com.deep.payload.request.FlightInstanceRequest;
import com.deep.payload.response.FlightInstanceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface FlightInstanceService {
    FlightInstanceResponse createFlightInstanceWithCabins(
            Long userId,
            FlightInstanceRequest request
    ) throws Exception;

    List<FlightInstanceResponse> getFlightInstances();

    FlightInstanceResponse getFlightInstanceById(Long id) throws Exception;

    Page<FlightInstanceResponse> getByAirlineId(
            Long userId,
            Long departureAirportId,
            Long arrivalAirportId,
            Long flightId,
            LocalDate onDate,
            Pageable pageable
    );

    FlightInstanceResponse updateFlightInstance(
            Long id,
            FlightInstanceRequest request
    ) throws Exception;

    void deleteFlightInstance(Long id);

    Map<Long, FlightInstanceResponse> getFlightInstancesByIds(List<Long> ids);
}
