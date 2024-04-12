package com.example.cinemaserver.Request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class OTPVerificationRequest {
    private String systemOTP;
    private LocalDateTime OTPExpirationTime;
    private String userOTP;
}
