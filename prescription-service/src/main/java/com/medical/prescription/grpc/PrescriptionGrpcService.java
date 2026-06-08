package com.medical.prescription.grpc;

import com.medical.grpc.PrescriptionRequest;      // Добавьте эти импорты
import com.medical.grpc.PrescriptionResponse;
import com.medical.grpc.RedeemRequest;
import com.medical.grpc.RedeemResponse;
import com.medical.grpc.PrescriptionServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

@GrpcService
public class PrescriptionGrpcService extends PrescriptionServiceGrpc.PrescriptionServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(PrescriptionGrpcService.class);

    // Простое in-memory хранилище для демо
    private final ConcurrentHashMap<String, String> prescriptionStatus = new ConcurrentHashMap<>();

    @Override
    public void issuePrescription(PrescriptionRequest request, StreamObserver<PrescriptionResponse> responseObserver) {
        log.info("=== issuePrescription called ===");
        log.info("Patient ID: {}", request.getPatientId());
        log.info("Doctor ID: {}", request.getDoctorId());
        log.info("Medication: {}", request.getMedicationName());
        log.info("Quantity: {}", request.getQuantity());

        try {
            // Генерируем ID рецепта
            String prescriptionId = UUID.randomUUID().toString();

            // Сохраняем статус
            prescriptionStatus.put(prescriptionId, "ISSUED");

            // Формируем ответ
            PrescriptionResponse response = PrescriptionResponse.newBuilder()
                    .setPrescriptionId(prescriptionId)
                    .setStatus("ISSUED")
                    .setMessage("Prescription issued successfully! Show this ID at pharmacy: " + prescriptionId)
                    .build();

            log.info("Response: {}", response);

            // Отправляем ответ
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("Error issuing prescription", e);
            responseObserver.onError(e);
        }
    }

    @Override
    public void redeemPrescription(RedeemRequest request, StreamObserver<RedeemResponse> responseObserver) {
        log.info("=== redeemPrescription called ===");
        log.info("Prescription ID: {}", request.getPrescriptionId());
        log.info("Pharmacy ID: {}", request.getPharmacyId());

        try {
            String prescriptionId = request.getPrescriptionId();
            String currentStatus = prescriptionStatus.get(prescriptionId);

            if (currentStatus == null) {
                RedeemResponse response = RedeemResponse.newBuilder()
                        .setSuccess(false)
                        .setMessage("Prescription not found!")
                        .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
                return;
            }

            if (!"ISSUED".equals(currentStatus)) {
                RedeemResponse response = RedeemResponse.newBuilder()
                        .setSuccess(false)
                        .setMessage("Prescription already redeemed or cancelled!")
                        .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
                return;
            }

            // Обновляем статус
            prescriptionStatus.put(prescriptionId, "REDEEMED");

            RedeemResponse response = RedeemResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Prescription redeemed successfully! Medication dispensed.")
                    .build();

            log.info("Response: {}", response);

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("Error redeeming prescription", e);
            responseObserver.onError(e);
        }
    }
}