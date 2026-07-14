package com.deep.service.impl;

import com.deep.mapper.CityMapper;
import com.deep.model.City;
import com.deep.payload.request.CityRequest;
import com.deep.payload.response.CityResponse;
import com.deep.service.CityService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.deep.repository.CityRepository;

@Service
@RequiredArgsConstructor
public class CityServiceImpl implements CityService {
    private final CityRepository cityRepository;

    @Override
    public CityResponse createCity(@NonNull CityRequest request) throws Exception {
        if(cityRepository.existsByCityCode(request.getCityCode())) {
            throw new Exception("city with given code already exists");
        }
        City city = CityMapper.toEntity(request);
        City result = cityRepository.save(city);
        return CityMapper.toResponse(result);
    }

    @Override
    public CityResponse getCityById(Long id) throws Exception {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new Exception("City not found with id: " + id));

        return CityMapper.toResponse(city);
    }

    @Override
    public CityResponse updateCity(Long id, @NonNull CityRequest request) throws Exception {
        City city = cityRepository.findById(id).orElseThrow(
                ()->new Exception("City not present with given Id")
        );
        if(cityRepository.existsByCityCode(request.getCityCode())) {
            throw new Exception("City with given code already exists.");
        }

        City updatedCity = cityRepository.save(CityMapper.updateEntity(city, request));
        return CityMapper.toResponse(updatedCity);
    }

    @Override
    public void deleteCity(Long id) throws Exception {
        City city = cityRepository.findById(id).orElseThrow(
                ()->new Exception("City not present with given Id")
        );
        cityRepository.delete(city);
    }

    @Override
    public Page<CityResponse> getAllCities(Pageable pageable) {
        return cityRepository.findAll(pageable).map(CityMapper::toResponse);
    }

    @Override
    public Page<CityResponse> searchCities(String keyword, Pageable pageable) {
        return cityRepository.searchByKeyword(keyword, pageable).map(CityMapper::toResponse);
    }

    @Override
    // here mapping is required for response conversion because the return type of getCitiesByCountryCode is Page<CityResponse>
    // but the return type of findByCountryCodeIgnoreCase is Page<City>
    public Page<CityResponse> getCitiesByCountryCode(String countryCode, Pageable pageable) {
        return cityRepository.findByCountryCodeIgnoreCase(countryCode, pageable).map(CityMapper::toResponse);
    }

    @Override
    public boolean cityExists(String cityCode) {
        return cityRepository.existsByCityCode(cityCode);
    }
}
