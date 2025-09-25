package com.aditya.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @NotBlank
    @Size(min = 3, message = "street name must be 3 or more characters long")
    private String street;

    @NotBlank
    @Size(min = 3, message = "building name must be 3 or more characters long")
    private String building;

    @NotBlank
    @Size(min = 3, message = "city name must be 3 or more characters long")
    private String city;

    @NotBlank
    @Size(min = 3, message = "state name must be 3 or more characters long")
    private String state;

    @NotBlank
    @Size(min = 3, message = "country name must be 3 or more characters long")
    private String country;

    @ToString.Exclude
    @ManyToMany(mappedBy = "addresses")
    private List<User> users = new ArrayList<>();

    public Address(String street, String building, String city, String state, String country) {
        this.street = street;
        this.building = building;
        this.city = city;
        this.state = state;
        this.country = country;
    }
}
