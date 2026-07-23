package com.deep.client;

import com.deep.payload.response.AirportResponse;

public class LocationClientFallback implements LocationClient{
    @Override
    public AirportResponse getAirportById(Long id) {
        return null;
    }
}
