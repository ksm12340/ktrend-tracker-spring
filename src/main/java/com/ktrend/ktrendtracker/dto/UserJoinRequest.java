package com.ktrend.ktrendtracker.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserJoinRequest {

    private String loginId;
    private String password;
    private String passwordCheck;
    private String name;
    private String email;
}