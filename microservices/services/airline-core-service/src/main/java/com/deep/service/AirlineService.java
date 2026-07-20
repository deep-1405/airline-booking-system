package com.deep.service;

import com.deep.enums.AirlineStatus;
import com.deep.payload.request.AirlineRequest;
import com.deep.payload.response.AirlineDropdownItem;
import com.deep.payload.response.AirlineResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AirlineService {

    // ----- CRUD -----
    AirlineResponse createAirline(AirlineRequest request, Long ownerId);
    AirlineResponse getAirlineByOwner(Long ownerId);
    AirlineResponse getAirlineById(Long id);
    Page<AirlineResponse> getAllAirlines(Pageable pageable);
    AirlineResponse updateAirline(AirlineRequest request, Long ownerId);
    void deleteAirline(Long id, Long ownerId);

    AirlineResponse changeStatusByAdmin(Long airlineId, AirlineStatus status);

    // ----- Dropdown -----
    List<AirlineDropdownItem> getAirlinesForDropdown();
}