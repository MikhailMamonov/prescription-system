package com.medical.prescription.client;

import com.medical.grpc.PrescriptionRequest;
import com.medical.grpc.PrescriptionResponse;
import com.medical.grpc.RedeemRequest;
import com.medical.grpc.RedeemResponse;
import com.medical.grpc.PrescriptionServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class PrescriptionGrpcClient {
    private final ManagedChannel channel;
    private final PrescriptionServiceGrpc.PrescriptionServiceBlockingStub stub;

    public PrescriptionGrpcClient(String host, int port) {
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        this.stub = PrescriptionServiceGrpc.newBlockingStub(channel);
    }

    public void issuePrescription(String patientId, String doctorId, String medication, int quantity) {
        PrescriptionRequest request = PrescriptionRequest.newBuilder()
                .setPatientId(patientId)
                .setDoctorId(doctorId)
                .setMedicationName(medication)
                .setQuantity(quantity)
                .build();

        PrescriptionResponse response = stub.issuePrescription(request);

        System.out.println("=== Issue Prescription Response ===");
        System.out.println("Prescription ID: " + response.getPrescriptionId());
        System.out.println("Status: " + response.getStatus());
        System.out.println("Message: " + response.getMessage());
        System.out.println();
    }

    public void redeemPrescription(String prescriptionId, String pharmacyId) {
        RedeemRequest request = RedeemRequest.newBuilder()
                .setPrescriptionId(prescriptionId)
                .setPharmacyId(pharmacyId)
                .build();

        RedeemResponse response = stub.redeemPrescription(request);

        System.out.println("=== Redeem Prescription Response ===");
        System.out.println("Success: " + response.getSuccess());
        System.out.println("Message: " + response.getMessage());
        System.out.println();
    }

    public void shutdown() {
        channel.shutdown();
    }

    public static void main(String[] args) {
        PrescriptionGrpcClient client = new PrescriptionGrpcClient("localhost", 9090);

        try {
            // 1. Выписываем рецепт
            client.issuePrescription("patient-123", "doctor-456", "Aspirin", 30);

            Thread.sleep(1000); // Небольшая пауза

            // 2. Пытаемся погасить рецепт (используйте реальный ID из первого ответа)
            // client.redeemPrescription("сюда-нужно-вставить-ID-из-ответа", "pharmacy-789");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.shutdown();
        }
    }
}