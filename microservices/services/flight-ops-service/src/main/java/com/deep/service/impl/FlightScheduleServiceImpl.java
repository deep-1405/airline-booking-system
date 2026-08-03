package com.deep.service.impl;

import com.deep.client.AirlineClient;
import com.deep.client.LocationClient;
import com.deep.enums.FlightStatus;
import com.deep.exception.ResourceNotFoundException;
import com.deep.mapper.FlightScheduleMapper;
import com.deep.model.Flight;
import com.deep.model.FlightSchedule;
import com.deep.payload.request.FlightInstanceRequest;
import com.deep.payload.response.AircraftResponse;
import com.deep.payload.response.AirlineResponse;
import com.deep.payload.response.AirportResponse;
import com.deep.payload.response.FlightScheduleResponse;
import com.deep.reposiotry.FlightRepository;
import com.deep.reposiotry.FlightScheduleRepository;
import com.deep.service.FlightInstanceService;
import com.deep.service.FlightScheduleService;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FlightScheduleServiceImpl implements FlightScheduleService {

    private final FlightScheduleRepository flightScheduleRepository;
    private final FlightRepository flightRepository;
    private final FlightInstanceService flightInstanceService;
    private final AirlineClient airlineClient;
    private final LocationClient locationClient;

    @Override
    public FlightScheduleResponse createFlightSchedule(Long userId, com.deep.payload.request.FlightScheduleRequest request) throws Exception {
        Flight flight = flightRepository.findById(request.getFlightId())
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found with id: " + request.getFlightId()));

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("End date must be after start date");
        }

        FlightSchedule schedule = FlightScheduleMapper.toEntity(request, flight);
        FlightSchedule savedSchedule = flightScheduleRepository.save(schedule);

        AircraftResponse aircraft = airlineClient.getAircraftById(flight.getAircraftId());

        List<DayOfWeek> operatingDays = schedule.getOperatingDays();
        LocalDate startDate = schedule.getStartDate();
        LocalDate endDate = schedule.getEndDate();

        FlightInstanceRequest flightInstanceRequest = FlightInstanceRequest.builder()
                .scheduleId(savedSchedule.getId())
                .flightId(flight.getId())
                .arrivalAirportId(flight.getArrivalAirportId())
                .departureAirportId(flight.getDepartureAirportId())
                .totalSeats(aircraft.getTotalSeats())
                .status(FlightStatus.SCHEDULED)
                .build();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            if (operatingDays.contains(date.getDayOfWeek())) {
                flightInstanceRequest.setDepartureDateTime(LocalDateTime.of(date, schedule.getDepartureTime()));
                flightInstanceRequest.setArrivalDateTime(LocalDateTime.of(date, schedule.getArrivalTime()));
                flightInstanceService.createFlightInstanceWithCabins(userId, flightInstanceRequest);
            }
        }
        return getFlightScheduleResponse(savedSchedule);
    }

    @Override
    public FlightScheduleResponse getFlightScheduleById(Long id) throws Exception {
        FlightSchedule schedule = flightScheduleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Flight schedule not found with id: " + id));
        return getFlightScheduleResponse(schedule);
    }

    @Override
    public List<FlightScheduleResponse> getFlightScheduleByAirline(Long userId) {
        AirlineResponse airline = airlineClient.getAirlineByOwner(userId);
        List<FlightSchedule> schedules = flightScheduleRepository.findByFlightAirlineId(airline.getId());
        return schedules.stream().map(s -> {
            try {
                return getFlightScheduleResponse(s);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }

    @Override
    public FlightScheduleResponse updateFlightSchedule(Long id, com.deep.payload.request.FlightScheduleRequest request) throws Exception {
        FlightSchedule existing = flightScheduleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Flight schedule not found with id: " + id));
        FlightScheduleMapper.updateEntity(request, existing);
        return getFlightScheduleResponse(flightScheduleRepository.save(existing));
    }

    @Override
    public void deleteFlightSchedule(Long id) {
        FlightSchedule schedule = flightScheduleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Flight schedule not found with id: " + id));
        flightScheduleRepository.delete(schedule);
    }

    private FlightScheduleResponse getFlightScheduleResponse(FlightSchedule schedule) {
        AirportResponse arrival = locationClient.getAirportById(schedule.getArrivalAirportId());
        AirportResponse departure = locationClient.getAirportById(schedule.getDepartureAirportId());
        return FlightScheduleMapper.toResponse(schedule, arrival, departure);
    }
}