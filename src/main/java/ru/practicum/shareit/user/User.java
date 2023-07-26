package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Email;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class User {
    private Integer id;
    @NonNull
    private String name;
    @NonNull
    @Email
    private String email;
}
