package com.deep.service.impl;

import com.deep.client.AirlineClient;
import com.deep.client.LocationClient;
//import com.deep.event.FlightInstanceCreatedEvent;
//import com.deep.event.FlightInstanceEventProducer;
import com.deep.mapper.FlightInstanceMapper;
import com.deep.mapper.FlightMapper;
import com.deep.model.Flight;
import com.deep.model.FlightInstance;
import com.deep.payload.request.FlightInstanceRequest;
import com.deep.payload.response.AircraftResponse;
import com.deep.payload.response.AirlineResponse;
import com.deep.payload.response.AirportResponse;
import com.deep.payload.response.FlightInstanceResponse;
import com.deep.reposiotry.FlightInstanceRepository;
import com.deep.reposiotry.FlightRepository;
import com.deep.service.FlightInstanceService;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class FlightInstanceServiceImpl implements FlightInstanceService {

    private final FlightInstanceRepository flightInstanceRepository;
    private final FlightRepository flightRepository;
    private final AirlineClient airlineClient;
//    private final FlightInstanceEventProducer flightInstanceEventProducer;
    private final LocationClient locationClient;

    @Override
    public FlightInstanceResponse createFlightInstanceWithCabins(
            Long userId,
            FlightInstanceRequest flightInstanceRequest
    ) throws Exception {

        Long airlineId = getAirlineForUser(userId);

        Flight flight = flightRepository.findById(airlineId).orElseThrow(
                () -> new ResourceNotFoundException("Flight not found")
        );

        AircraftResponse aircraftResponse = getAircraftById(flight.getAircraftId());

        FlightInstance flightInstance = FlightInstanceMapper.toEntity(flightInstanceRequest, flight);

        flightInstance.setAirlineId(airlineId);
        flightInstance.setFlight(flight);
        flightInstance.setDepartureAirportId(flightInstanceRequest.getDepartureAirportId());
        flightInstance.setArrivalAirportId(flightInstanceRequest.getArrivalAirportId());

        // taking seats from the aircraftResponse
        flightInstance.setTotalSeats(aircraftResponse.getTotalSeats());
        flightInstance.setAvailableSeats(aircraftResponse.getTotalSeats());

        FlightInstance savedFlightInstance = flightInstanceRepository.save(flightInstance);

//        flightInstanceEventProducer.sendFlightInstanceCreated(
//                FlightInstanceCreatedEvent
//                .builder()
//                .flightInstanceId(savedFlightInstance.getId())
//                .aircraftId(flight.getAircraftId())
//                .flightId(flight.getId())
//                .build()
//        );
//        System.out.println("Publish event for seat-service to create FlightInstanceCabins ----- ");

        return getFlightInstance(savedFlightInstance);
    }

    @Override
    public List<FlightInstanceResponse> getFlightInstances() {
        return flightInstanceRepository
                .findAll()
                .stream()
                .map(
                        flightInstance -> {
                            try {
                                return getFlightInstance(flightInstance);
                            } catch (Exception e) {
                                try {
                                    throw new Exception(e);
                                } catch (Exception ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                        }
                )
                .toList();
    }

    @Override
    public FlightInstanceResponse getFlightInstanceById(Long id) throws Exception {
        FlightInstance flightInstance = flightInstanceRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Flight instance not found with id: " + id)
        );
        return getFlightInstance(flightInstance);
    }

    @Override
    public Page<FlightInstanceResponse> getByAirlineId(
            Long userId,
            Long departureAirportId,
            Long arrivalAirportId,
            Long flightId,
            LocalDate onDate,
            Pageable pageable
    ) {

        Long airlineId = getAirlineForUser(userId);
        LocalDateTime start = onDate != null ? onDate.atStartOfDay() : null;
        LocalDateTime end = onDate != null ? onDate.plusDays(1).atStartOfDay() : null;

        return flightInstanceRepository.findByAirlineIdWithFilters(
                airlineId,
                departureAirportId,
                arrivalAirportId,
                flightId,
                start,
                end,
                pageable
        ).map(
                flightInstance -> {
                    try {
                        return getFlightInstance(flightInstance);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }

    @Override
    public FlightInstanceResponse updateFlightInstance(Long id, FlightInstanceRequest request) throws Exception {

        FlightInstance existing = flightInstanceRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Flight instance not found with id: " + id)
        );

        FlightInstanceMapper.updateEntity(request, existing);
        return getFlightInstance(flightInstanceRepository.save(existing));
    }

    @Override
    public void deleteFlightInstance(Long id) {
        FlightInstance fi = flightInstanceRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Flight instance not found with id: " + id)
        );
        flightInstanceRepository.delete(fi);
    }

    @Override
    public Map<Long, FlightInstanceResponse> getFlightInstancesByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return Map.of();
        List<FlightInstance> instances = flightInstanceRepository.findAllByIdInWithFlight(ids);

        Map<Long, AirlineResponse> airlineCache = new HashMap<>();
        Map<Long, AircraftResponse> aircraftCache = new HashMap<>();
        Map<Long, AirportResponse> airportCache = new HashMap<>();

        Map<Long, FlightInstanceResponse> result = new HashMap<>();
        for (FlightInstance fi : instances) {
            AirlineResponse airline = airlineCache.computeIfAbsent(fi.getAirlineId(), airlineClient::getAirlineById);
            AircraftResponse aircraft = aircraftCache.computeIfAbsent(fi.getFlight().getAircraftId(), airlineClient::getAircraftById);
            AirportResponse departure = airportCache.computeIfAbsent(fi.getDepartureAirportId(), locationClient::getAirportById);
            AirportResponse arrival = airportCache.computeIfAbsent(fi.getArrivalAirportId(), locationClient::getAirportById);
            result.put(fi.getId(), FlightInstanceMapper.toResponse(fi, aircraft, airline, departure, arrival));
        }
        return result;
    }


    private AircraftResponse getAircraftById(Long aircraftId) {
        try {
            return airlineClient.getAircraftById(aircraftId);
        } catch (FeignException.NotFound e) {
            throw new EntityNotFoundException("No aircraft found for id: " + aircraftId);
        } catch (FeignException e) {
            throw new RuntimeException(
                "Failed to fetch aircraft from airline-core-service: " + e.getMessage(), e
            );
        }
    }

    private Long getAirlineForUser(Long userId) {
        try {
            AirlineResponse airline = airlineClient.getAirlineByOwner(userId);
            return airline.getId();
        } catch (FeignException.NotFound e) {
            throw new EntityNotFoundException("No airline found for user: " + userId);
        } catch (FeignException e) {
            throw new RuntimeException("Failed to fetch airline from airline-core-service: " + e.getMessage(), e);
        }
    }

    private FlightInstanceResponse getFlightInstance(FlightInstance fi) throws Exception {
        AirlineResponse airline = airlineClient.getAirlineById(fi.getAirlineId());
        AirportResponse departureAirport = locationClient.getAirportById(fi.getDepartureAirportId());
        AirportResponse arrivalAirport = locationClient.getAirportById(fi.getArrivalAirportId());
        AircraftResponse aircraftResponse = airlineClient.getAircraftById(fi.getFlight().getAircraftId());
        return FlightInstanceMapper.toResponse(
            fi,
            aircraftResponse,
            airline,
            departureAirport,
            arrivalAirport
        );
    }
}
