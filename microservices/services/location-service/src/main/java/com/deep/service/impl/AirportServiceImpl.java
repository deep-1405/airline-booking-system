package com.deep.service.impl;

import com.deep.mapper.AirportMapper;
import com.deep.model.Airport;
import com.deep.model.City;
import com.deep.payload.request.AirportRequest;
import com.deep.payload.response.AirportResponse;
import com.deep.repository.AirportRepository;
import com.deep.repository.CityRepository;
import com.deep.service.AirportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AirportServiceImpl implements AirportService {

    private final AirportRepository airportRepository;
    private  final CityRepository cityRepository;

    @Override
    public AirportResponse createAirport(AirportRequest request) throws Exception {
        if(airportRepository.findByIataCode(request.getIataCode()).isPresent()) {
            throw new Exception("Airport with Iata code" + request.getIataCode() + "already exists");
        }

        City city = cityRepository.findById(request.getCityId())
                .orElseThrow(() -> new Exception("City not found"));

        Airport airport = AirportMapper.toEntity(request);
        airport.setCity(city);

        Airport savedAirport = airportRepository.save(airport);

        return AirportMapper.toResponse(savedAirport);
    }

    @Override
    public AirportResponse getAirportById(Long id) throws Exception {
        Airport airport = airportRepository.findById(id).orElseThrow(
                ()->new Exception("airport not exist with provided id")
        );

        return AirportMapper.toResponse(airport);
    }

    @Override
    public List<AirportResponse> getAllAirports() {
        return airportRepository.findAll().stream()
                .map(AirportMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AirportResponse updateAirport(Long id, AirportRequest request) throws Exception {
        // check for edge case if there is an airport existing with the corresponding id or not
        Airport existingAirport = airportRepository.findById(id).orElseThrow(
                // throwing exception upon not finding an airport with an ID
                () -> new Exception("airport not exist with id" + id)
        );
        // if the request is emtpy or
        // existing airport is having same iata code as the request iata code or
        // there already exists and airport with the same iatacode
        // then throw and exception
        if(
                request.getIataCode() != null
                && !existingAirport.getIataCode().equals(request.getIataCode())
                && airportRepository.findByIataCode(request.getIataCode()).isPresent()
        ){
            throw new Exception("Airport with Iata code already exists");
        }

        // updating existing airport with the request payload
        AirportMapper.updateEntity(existingAirport, request);

        // saving updated existing airport
        Airport updatedAirport = airportRepository.save(existingAirport);

        // returning the updated airport
        return AirportMapper.toResponse(updatedAirport);
    }

    @Override
    public void deleteAirport(Long id) throws Exception {
        // check for edge case if there is an airport existing with the corresponding id or not
        Airport airport = airportRepository.findById(id).orElseThrow(
                // throwing exception upon not finding an airport with an ID
                () -> new Exception("airport not exist with id" + id)
        );

        airportRepository.delete(airport);
    }

    @Override
    public List<AirportResponse> getAirportByCityId(Long cityId) {
        return airportRepository.findByCityId(cityId).stream()
                .map(AirportMapper::toResponse)
                .collect(Collectors.toList());
    }
}
