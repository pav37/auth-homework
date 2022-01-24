package ru.myapp.model;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UserDto {
    private Long id;
    @NotNull
    @NotEmpty
    private String username;
    @NotNull
    @NotEmpty
    private String firstName;
    @NotNull
    @NotEmpty
    private String lastName;
    private String email;
}