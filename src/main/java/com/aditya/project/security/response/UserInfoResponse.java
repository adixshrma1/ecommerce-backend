package com.aditya.project.security.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {
    private Long id;
//    private String jwt;   // not needed, in cookie based auth
    private String username;
    private List<String> roles;
}
