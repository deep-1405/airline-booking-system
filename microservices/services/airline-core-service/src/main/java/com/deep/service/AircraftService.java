package com.deep.service;

import com.deep.payload.request.AircraftRequest;
import com.deep.payload.response.AircraftResponse;

import java.util.List;

public interface AircraftService {

    AircraftResponse createAircraft(AircraftRequest request, Long ownerId);
    AircraftResponse getAircraftById(Long id);
    List<AircraftResponse> listAllAircraftByOwner(Long ownerId);
    AircraftResponse updateAircraft(Long id, AircraftRequest request, Long ownerId);
    void deleteAircraft(Long id);
}
