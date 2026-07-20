package com.deep.service.impl;

import com.deep.mapper.AircraftMapper;
import com.deep.model.Aircraft;
import com.deep.model.Airline;
import com.deep.payload.request.AircraftRequest;
import com.deep.payload.response.AircraftResponse;
import com.deep.repository.AircraftRepository;
import com.deep.repository.AirlineRepository;
import com.deep.service.AircraftService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AircraftServiceImpl implements AircraftService {

    private final AirlineRepository airlineRepository;
    private final AircraftRepository aircraftRepository;

    @Override
    public AircraftResponse createAircraft(AircraftRequest request, Long ownerId) {
        Airline airline = airlineRepository
                .findByOwnerId(ownerId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Airline not found for owner: " + ownerId)
                );

        // missed to add check for already existing aircraft code
        Aircraft aircraft = AircraftMapper.toEntity(request, airline);
        if(aircraftRepository.existsByCode(aircraft.getCode())) {
            throw new IllegalArgumentException("Aircraft with code " + aircraft.getCode() + " already exists");
        }

        validateAircraftData(aircraft);

        return AircraftMapper.toResponse(aircraftRepository.save(aircraft));
    }

    @Override
    public AircraftResponse getAircraftById(Long id) {
        return AircraftMapper.toResponse(aircraftRepository.findById(id).orElseThrow(
                ()-> new EntityNotFoundException("Aircraft with " + id + "  Id not found")
        ));
    }

    @Override
    public List<AircraftResponse> listAllAircraftsByOwner(Long ownerId) {
        Airline airline = airlineRepository.
                findByOwnerId(ownerId).
                orElseThrow(
                        () -> new EntityNotFoundException("Airline with " + ownerId + " Owner Id not found")
                );
        List<Aircraft> aircraft = aircraftRepository.findByAirline(airline);
        return aircraft
                .stream()
                .map(AircraftMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AircraftResponse updateAircraft(Long id, AircraftRequest request, Long ownerId) {
        Airline airline = airlineRepository.
                findByOwnerId(ownerId).
                orElseThrow(
                        () -> new EntityNotFoundException("Airline with " + ownerId + " Owner Id not found")
                );
        Aircraft aircraft = aircraftRepository
                .findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Aircraft with " + id + " Id not found")
                );
        
        String oldCode = aircraft.getCode();
        // checking if the aircraft code is changed or not and if so if there is an aircraft already with the new code
        if (request.getCode() != null &&
                !oldCode.equals(request.getCode()) &&
                aircraftRepository.existsByCode(request.getCode())) {

            throw new IllegalArgumentException(
                    "Aircraft with code " + request.getCode() + " already exists");
        }

        // updating aircraft entity
        AircraftMapper.updateEntity(aircraft, request, airline);

        // validating updated entity
        validateAircraftData(aircraft);

        // saving the updating entity into database
        aircraftRepository.save(aircraft);

        return AircraftMapper.toResponse(aircraft);
    }

    @Override
    public void deleteAircraft(Long id) {
        Aircraft aircraft = aircraftRepository
                .findById(id)
                .orElseThrow(
                ()-> new EntityNotFoundException("Aircraft with Id" + id + " not found")
        );
        aircraftRepository.delete(aircraft);
    }

    private void validateAircraftData(Aircraft aircraft) {

        int totalSeats = aircraft.getTotalSeats();

        if(totalSeats > aircraft.getSeatingCapacity()) {
            throw new IllegalArgumentException("Total specified seats exceed aircraft seating capacity");
        }

        if (aircraft.getYearOfManufacture() != null &&
                (aircraft.getYearOfManufacture() < 1900
                        || aircraft.getYearOfManufacture() > LocalDate.now().getYear())) {
            throw new IllegalArgumentException("Invalid year of manufacture");
        }
    }
}
