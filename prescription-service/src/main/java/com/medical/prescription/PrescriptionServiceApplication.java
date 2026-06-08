package com.medical.prescription;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PrescriptionServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PrescriptionServiceApplication.class, args);
        System.out.println("Prescription Service started with gRPC on port 9090");
    }
}