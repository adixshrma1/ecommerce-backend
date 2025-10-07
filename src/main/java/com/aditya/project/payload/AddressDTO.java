package com.aditya.project.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {
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

    @NotBlank
    @Size(min = 5, message = "pincode must be 5 or more characters long")
    private String pincode;

}
