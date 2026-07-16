package com.deep.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
// used in Mappers through .builder()
@Builder
public class CityRequest {

    // All the validation annotations are triggered inside the controllers through @Valid annotation
    // Instead of using new CityResponse(...), the @Builder allows the Mapper to create these objects in a clean, readable way.
    @NotBlank(message = "City name is required")
    @Size(max = 100)
    private String name;

    @NotBlank(message = "City code is required")
    @Size(max = 10)
    private String cityCode;

    @NotBlank(message = "Country code is required")
    @Size(max = 5)
    private String countryCode;

    @NotBlank(message = "Country name is required")
    @Size(max = 100)
    private String countryName;

    @Size(max = 10)
    private String regionCode;

    @Size(max = 10)
    private String timeZoneOffset;
}
