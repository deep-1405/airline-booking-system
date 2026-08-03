package com.deep.service.impl;

import com.deep.client.AirlineClient;
import com.deep.client.LocationClient;
import com.deep.client.PricingClient;
import com.deep.client.SeatClient;
import com.deep.mapper.FlightInstanceMapper;
import com.deep.model.FlightInstance;
import com.deep.payload.request.FlightSearchRequest;
import com.deep.payload.response.*;
import com.deep.reposiotry.FlightInstanceRepository;
import com.deep.service.FlightSearchService;
import com.deep.specification.FlightInstanceSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlightSearchServiceImpl implements FlightSearchService {

    private final FlightInstanceRepository flightInstanceRepository;
    private final LocationClient locationClient;
    private final AirlineClient airlineClient;
    private final PricingClient pricingClient;
    private final SeatClient seatClient;

    @Override
    @Transactional
    public Page<FlightInstanceResponse> searchFlights(FlightSearchRequest request, Pageable pageable) {
        Pageable sortedPageable = applySort(pageable, request.getSortBy(), request.getSortOrder());
        Specification<FlightInstance> spec = FlightInstanceSpecification.buildSearchSpec(request);
        Page<FlightInstance> dbPage = flightInstanceRepository.findAll(spec, sortedPageable);

        if (dbPage.isEmpty()) return Page.empty(sortedPageable);

        List<FlightInstance> instances = new ArrayList<>(dbPage.getContent());
        Map<Long, FareResponse> fareMap = new HashMap<>();

        if (request.getCabinClass() != null) {
            List<FlightInstance> filtered = new ArrayList<>();
            for (FlightInstance fi : instances) {
                CabinClassResponse cabin = seatClient.getCabinClassByAircraftIdAndName(
                        request.getCabinClass(), fi.getFlight().getAircraftId());
                if (cabin == null) continue; // seat-service down or aircraft lacks this cabin — skip, don't crash

                FareResponse fare = pricingClient.getLowestFareForFlightAndCabinClass(fi.getFlight().getId(), cabin.getId());
                if (fare == null) continue;

                if (request.getMinPrice() != null && fare.getTotalPrice() < request.getMinPrice()) continue;
                if (request.getMaxPrice() != null && fare.getTotalPrice() > request.getMaxPrice()) continue;

                fareMap.put(fi.getFlight().getId(), fare);
                filtered.add(fi);
            }
            instances = filtered;
            if (instances.isEmpty()) return Page.empty(sortedPageable);
        }

        List<FlightInstanceResponse> responses = enrich(instances, fareMap);
        return new PageImpl<>(responses, sortedPageable, dbPage.getTotalElements());
    }

    private List<FlightInstanceResponse> enrich(List<FlightInstance> instances, Map<Long, FareResponse> fareMap) {
        Map<Long, AirlineResponse> airlineCache = new HashMap<>();
        Map<Long, AirportResponse> airportCache = new HashMap<>();
        Map<Long, AircraftResponse> aircraftCache = new HashMap<>();
        List<FlightInstanceResponse> results = new ArrayList<>();

        for (FlightInstance fi : instances) {
            try {
                AircraftResponse aircraft = aircraftCache.computeIfAbsent(fi.getFlight().getAircraftId(), airlineClient::getAircraftById);
                AirlineResponse airline = airlineCache.computeIfAbsent(fi.getAirlineId(), airlineClient::getAirlineById);
                AirportResponse dep = airportCache.computeIfAbsent(fi.getDepartureAirportId(), locationClient::getAirportById);
                AirportResponse arr = airportCache.computeIfAbsent(fi.getArrivalAirportId(), locationClient::getAirportById);
                FlightInstanceResponse resp = FlightInstanceMapper.toResponse(fi, aircraft, airline, dep, arr);
                resp.setFare(fareMap.get(fi.getFlight().getId()));
                results.add(resp);
            } catch (Exception e) {
                log.warn("Skipping instance {} — enrichment failed: {}", fi.getId(), e.getMessage());
            }
        }
        return results;
    }

    private Pageable applySort(Pageable pageable, String sortBy, String sortOrder) {
        Sort.Direction dir = "desc".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = switch (sortBy == null ? "" : sortBy.toLowerCase()) {
            case "arrival" -> Sort.by(dir, "arrivalDateTime");
            default -> Sort.by(dir, "departureDateTime");
        };
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }
}