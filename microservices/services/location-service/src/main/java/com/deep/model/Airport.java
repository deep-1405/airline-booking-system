package com.deep.model;

import com.deep.embeddable.Address;
import com.deep.embeddable.GeoCode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Airport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false, length = 3)
    private String iataCode;

    @Column(nullable = false)
    private String name;

    @Embedded
    private Address address;

    @Embedded
    private GeoCode geoCode;

    @Column(name = "time_zone_id", length = 50)
    private String timeZone;

    @ManyToOne
    @JsonIgnore
    // JsonIgnore prevents the jackson library from looking at the city field inside the airport entity class only
    // to prevent the app from crashing if the entity is ever used directly in a JSON context
    // e.g. if city is dependent on the airport also than it will crash because of the situation like: Airport->City->Airport->City->Airport
    private City city;

    @JsonIgnore
    @Transient
    public String getDetailedName() {
        if(city != null && city.getCountryCode() != null) {
            return name.toUpperCase() + "/" + city.getCountryCode();
        }
        return name.toUpperCase();
    }
}
