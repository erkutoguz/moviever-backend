package com.erkutoguz.moviever_backend.model;

import java.util.Random;
import java.util.UUID;

public class Otp {

    private final String otp;
    public Otp() {
        this.otp = String.valueOf(UUID.randomUUID());
    }
}
