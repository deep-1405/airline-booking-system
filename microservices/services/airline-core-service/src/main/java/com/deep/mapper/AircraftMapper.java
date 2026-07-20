package com.deep.mapper;

import com.deep.model.Aircraft;
import com.deep.model.Airline;
import com.deep.payload.request.AircraftRequest;
import com.deep.payload.response.AircraftResponse;

public class AircraftMapper {

    public static Aircraft toEntity(AircraftRequest request, Airline airline) {
        if(request == null) return null;

        return Aircraft
                .builder()
                .code(request.getCode())
                .model(request.getModel())
                .manufacturer(request.getManufacturer())
                .seatingCapacity(request.getSeatingCapacity())
                .economySeats(request.getEconomySeats())
                .premiumEconomySeats(request.getPremiumEconomySeats())
                .businessSeats(request.getBusinessSeats())
                .firstClassSeats(request.getFirstClassSeats())
                .rangeKm(request.getRangeKm())
                .cruisingSpeedKmh(request.getCruisingSpeedKmh())
                .maxAltitudeFt(request.getMaxAltitudeFt())
                .yearOfManufacture(request.getYearOfManufacture())
                .registrationDate(request.getRegistrationDate())
                .nextMaintenanceDate(request.getNextMaintenanceDate())
                .status(request.getStatus())
                .isAvailable(request.getIsAvailable())
                .airline(airline)
                .currentAirportId(request.getCurrentAirportId())
                .build();
    }

    public static AircraftResponse toResponse(Aircraft aircraft) {
        if(aircraft == null) return null;

        return AircraftResponse.builder()
                .id(aircraft.getId())
                .code(aircraft.getCode())
                .model(aircraft.getModel())
                .manufacturer(aircraft.getManufacturer())
                .seatingCapacity(aircraft.getSeatingCapacity())
                .economySeats(aircraft.getEconomySeats())
                .premiumEconomySeats(aircraft.getPremiumEconomySeats())
                .businessSeats(aircraft.getBusinessSeats())
                .firstClassSeats(aircraft.getFirstClassSeats())
                .rangeKm(aircraft.getRangeKm())
                .cruisingSpeedKmh(aircraft.getCruisingSpeedKmh())
                .maxAltitudeFt(aircraft.getMaxAltitudeFt())
                .yearOfManufacture(aircraft.getYearOfManufacture())
                .registrationDate(aircraft.getRegistrationDate())
                .nextMaintenanceDate(aircraft.getNextMaintenanceDate())
                .status(aircraft.getStatus())
                .isAvailable(aircraft.getIsAvailable())
                // Airline info (same service — available directly)
                .airlineId(aircraft.getAirline() != null ? aircraft.getAirline().getId() : null)
                .airlineName(aircraft.getAirline() != null ? aircraft.getAirline().getName() : null)
                .airlineIataCode(aircraft.getAirline() != null ? aircraft.getAirline().getIataCode() : null)
                // Airport is cross-service — only ID available here
                .currentAirportId(aircraft.getCurrentAirportId())
                // Computed fields
                .totalSeats(aircraft.getTotalSeats())
                .requiresMaintenance(aircraft.requiresMaintenance())
                .isOperational(aircraft.isOperational())
                // Audit
                .createdAt(aircraft.getCreatedAt())
                .updatedAt(aircraft.getUpdatedAt())
                .build();
    }

    public static void updateEntity(Aircraft aircraft, AircraftRequest request, Airline airline) {
        if (aircraft == null || request == null) return;

        if (request.getCode() != null)
            aircraft.setCode(request.getCode());

        if (request.getModel() != null)
            aircraft.setModel(request.getModel());

        if (request.getManufacturer() != null)
            aircraft.setManufacturer(request.getManufacturer());

        if (request.getSeatingCapacity() != null)
            aircraft.setSeatingCapacity(request.getSeatingCapacity());

        if (request.getEconomySeats() != null)
            aircraft.setEconomySeats(request.getEconomySeats());

        if (request.getPremiumEconomySeats() != null)
            aircraft.setPremiumEconomySeats(request.getPremiumEconomySeats());

        if (request.getBusinessSeats() != null)
            aircraft.setBusinessSeats(request.getBusinessSeats());

        if (request.getFirstClassSeats() != null)
            aircraft.setFirstClassSeats(request.getFirstClassSeats());

        if (request.getRangeKm() != null)
            aircraft.setRangeKm(request.getRangeKm());

        if (request.getCruisingSpeedKmh() != null)
            aircraft.setCruisingSpeedKmh(request.getCruisingSpeedKmh());

        if (request.getMaxAltitudeFt() != null)
            aircraft.setMaxAltitudeFt(request.getMaxAltitudeFt());

        if (request.getYearOfManufacture() != null)
            aircraft.setYearOfManufacture(request.getYearOfManufacture());

        if (request.getRegistrationDate() != null)
            aircraft.setRegistrationDate(request.getRegistrationDate());

        if (request.getNextMaintenanceDate() != null)
            aircraft.setNextMaintenanceDate(request.getNextMaintenanceDate());

        if (request.getStatus() != null)
            aircraft.setStatus(request.getStatus());

        if (request.getIsAvailable() != null)
            aircraft.setIsAvailable(request.getIsAvailable());

        if (airline != null)
            aircraft.setAirline(airline);

        if (request.getCurrentAirportId() != null)
            aircraft.setCurrentAirportId(request.getCurrentAirportId());
    }
}
