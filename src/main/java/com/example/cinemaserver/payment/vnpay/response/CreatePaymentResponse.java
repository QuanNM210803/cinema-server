package com.example.cinemaserver.payment.vnpay.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreatePaymentResponse implements Serializable {
    private String URL;
}
