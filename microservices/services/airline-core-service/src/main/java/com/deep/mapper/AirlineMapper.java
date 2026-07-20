package com.deep.mapper;

import com.deep.embeddable.Support;
import com.deep.model.Airline;
import com.deep.payload.request.AirlineRequest;
import com.deep.payload.response.AirlineResponse;

public class AirlineMapper {

    public static Airline toEntity(AirlineRequest request, Long ownerId) {
        if(request == null) return  null;

        Airline airline = Airline.builder()
                .iataCode(request.getIataCode())
                .icaoCode(request.getIcaoCode())
                .name(request.getName())
                .alias(request.getAlias())
                .country(request.getCountry())
                .logoUrl(request.getLogoUrl())
                .website(request.getWebsite())
                .status(request.getStatus())
                .alliance(request.getAlliance())
                .headquartersCityId(request.getHeadquartersCityId())
                .ownerId(ownerId)
                .build();

        if(request.getSupportEmail() != null
            || request.getSupportHours() != null
            || request.getSupportPhone() != null) {
            airline.setSupport(
                Support.builder()
                .email(request.getSupportEmail())
                .phone(request.getSupportPhone())
                .hours(request.getSupportHours())
                .build()
            );
        }

        return airline;
    }

    public static AirlineResponse toResponse(Airline airline) {
        if(airline == null) return null;

        return AirlineResponse.builder()
                .id(airline.getId())
                .iataCode(airline.getIataCode())
                .icaoCode(airline.getIcaoCode())
                .name(airline.getName())
                .alias(airline.getAlias())
                .country(airline.getCountry())
                .logoUrl(airline.getLogoUrl())
                .website(airline.getWebsite())
                .status(airline.getStatus())
                .alliance(airline.getAlliance())
                .support(airline.getSupport())
                .headquartersCityId(airline.getHeadquartersCityId())
                .createdAt(airline.getCreatedAt())
                .updatedAt(airline.getUpdatedAt())
                .ownerId(airline.getOwnerId())
                .updatedById(airline.getUpdatedById())
                .build();
    }

    public static void updateEntity(Airline airline, AirlineRequest request) {

        if (request.getIataCode() != null)
            airline.setIataCode(request.getIataCode());

        if (request.getIcaoCode() != null)
            airline.setIcaoCode(request.getIcaoCode());

        if (request.getName() != null)
            airline.setName(request.getName());

        if (request.getAlias() != null)
            airline.setAlias(request.getAlias());

        if (request.getCountry() != null)
            airline.setCountry(request.getCountry());

        if (request.getLogoUrl() != null)
            airline.setLogoUrl(request.getLogoUrl());

        if (request.getWebsite() != null)
            airline.setWebsite(request.getWebsite());

        if (request.getStatus() != null)
            airline.setStatus(request.getStatus());

        if (request.getAlliance() != null)
            airline.setAlliance(request.getAlliance());

        if (request.getHeadquartersCityId() != null)
            airline.setHeadquartersCityId(request.getHeadquartersCityId());

        if (request.getSupportEmail() != null ||
                request.getSupportPhone() != null ||
                request.getSupportHours() != null) {

            if (airline.getSupport() == null) {
                airline.setSupport(new Support());
            }

            if (request.getSupportEmail() != null)
                airline.getSupport().setEmail(request.getSupportEmail());

            if (request.getSupportPhone() != null)
                airline.getSupport().setPhone(request.getSupportPhone());

            if (request.getSupportHours() != null)
                airline.getSupport().setHours(request.getSupportHours());
        }
    }
}
