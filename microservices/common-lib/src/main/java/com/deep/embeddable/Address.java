package com.deep.embeddable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// This is related to the JPA, it tells the JPA, that this class does not need its own table, instead, take its fields (Street, postalcode)
// and put them directly into the table of any class that uses it
// Used in the Airport.java Entity (Database level)
@Embeddable
// provides getters, setters, equals() and toString()
@Data
// Required by JPA/Hibernate so it can create and empty object before filling it with data from the database
@NoArgsConstructor
// Allows for easy object creation
@AllArgsConstructor
// getter and setter used in AirportMapper
@Builder
public class Address {
    private String street;
    private String postalCode;
}
