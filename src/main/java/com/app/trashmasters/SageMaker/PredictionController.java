package com.app.trashmasters.SageMaker;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/predictions")
@Tag(name = "Predictions", description = "ML prediction management — manual trigger for SageMaker batch updates")
public class PredictionController {

    private final PredictionScheduler predictionScheduler;

    // Inject the scheduler we just built so we can call its methods manually
    public PredictionController(PredictionScheduler predictionScheduler) {
        this.predictionScheduler = predictionScheduler;
    }

    // This exposes a POST endpoint that Terry can hit from React
    @Operation(summary = "Force prediction update", description = "Manually triggers the hourly SageMaker prediction batch for all bins")
    @PostMapping("/force-update")
    public ResponseEntity<String> forceUpdatePredictions() {
        System.out.println("Manual prediction update triggered by frontend!");

        try {
            // Manually run the exact same function the hourly timer uses
            predictionScheduler.updateAllBinPredictions();

            return ResponseEntity.ok("✅ All bins successfully updated with fresh AI predictions!");
        } catch (Exception e) {
            System.err.println("❌ Manual update failed: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Failed to update predictions: " + e.getMessage());
        }
    }
}
