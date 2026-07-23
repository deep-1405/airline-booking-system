package com.deep.client;

import com.deep.payload.response.AircraftResponse;
import com.deep.payload.response.AirlineResponse;

import java.util.Collections;
import java.util.List;

public class AirlineClientFallback implements AirlineClient{
    @Override
    public AirlineResponse getAirlineByOwner(Long userId) {
        return null;
    }

    @Override
    public AirlineResponse getAirlineById(Long airlineId) {
        return null;
    }

    @Override
    public AircraftResponse getAircraftById(Long id) {
        return null;
    }

    @Override
    public List<AirlineResponse> getAirlinesByIataCodes(List<String> codes) {
        return Collections.emptyList();
    }

    @Override
    public List<AirlineResponse> getAirlinesByAlliance(String alliance) {
        return Collections.emptyList();
    }
}
