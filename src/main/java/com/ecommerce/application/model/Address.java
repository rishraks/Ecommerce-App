package com.ecommerce.application.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long addressId;

    @NotBlank
    @Size(min = 4, message = "Street name must contain at least 4 characters!")
    private String street;

    @NotBlank
    @Size(min = 4, message = "Building name must contain at least 4 characters!")
    private String buildingName;

    @NotBlank
    @Size(min = 3, message = "City name must contain at least 4 characters!")
    private String city;

    @NotBlank
    @Size(min = 4, message = "Country name must contain at least 4 characters!")
    private String country;

    @NotBlank
    @Size(min = 6, message = "Pincode name must contain at least 4 characters!")
    private String pincode;

    @NotBlank
    @Size(min = 10, max = 10, message = "Phone number should be of 10 digits")
    private String phoneNumber;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Address address)) return false;
        return Objects.equals(street, address.street) && Objects.equals(buildingName, address.buildingName) &&
                Objects.equals(city, address.city) && Objects.equals(country, address.country) && Objects.equals(pincode, address.pincode)
                && Objects.equals(phoneNumber, address.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, buildingName, city, country, pincode, phoneNumber);
    }
}
