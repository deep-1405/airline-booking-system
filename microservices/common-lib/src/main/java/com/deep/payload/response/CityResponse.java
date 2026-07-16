package com.deep.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

// there is no validation checks or null checks being carried out for the CityResponse or the AirportResponse because response is generated from the repository
// and repository has data that is stored after processing and checking validity from the request so no need to add the checks or anything here
public class CityResponse {

    private long id;
    private String name;
    private String cityCode;
    private String countryName;
    private String countryCode;
    private String regionCode;
    private String timeZoneOffset;
}
