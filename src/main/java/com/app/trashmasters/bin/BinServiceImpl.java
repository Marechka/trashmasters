package com.app.trashmasters.bin;

import com.app.trashmasters.bin.dto.BinCreateRequest;
import com.app.trashmasters.bin.model.BinStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.app.trashmasters.bin.model.Bin;


import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BinServiceImpl implements BinService {

    private final BinRepository binRepository;

    @Autowired
    public BinServiceImpl(BinRepository binRepository) {
        this.binRepository = binRepository;
    }

    @Override
    public List<Bin> getAllBins() {
        return binRepository.findAll();
    }

    @Override
    public Bin createBin(BinCreateRequest request) {
        Bin bin = new Bin();

        // Manual Mapping (Safest and clearest)
        bin.setBinId(request.getBinId());
        bin.setLocationName(request.getLocationName());
        bin.setLatitude(request.getLatitude());
        bin.setLongitude(request.getLongitude());
        bin.setDepthCm(request.getDepthCm());
        bin.setFillLevel(request.getFillLevel());
        bin.setSensorId(request.getSensorId());
        bin.setLastUpdated(Instant.now());

        // Handle optional prediction data
        if (request.getPredictedFillLevel() != null) {
            bin.setPredictedFillLevel(request.getPredictedFillLevel());
            bin.setPredictionTargetTime(request.getPredictionTargetTime());
        }

        // Default values for new bins
        bin.setFlagged(false);

        return binRepository.save(bin);
    }

    @Override
    public Bin getBinByBinId(String id) {
        return binRepository.findByBinId(id)
                .orElseThrow(() -> new RuntimeException("Bin not found with id: " + id));
    }

    @Override
    public Bin setFlag(String binId, boolean isFlagged, String issue) {
        Bin bin = binRepository.findByBinId(binId)
                .orElseThrow(() -> new RuntimeException("Bin not found"));

        bin.setFlagged(isFlagged);

        if (isFlagged) {
            // If flagging: Save the issue and force status to MAINTENANCE
            bin.setIssue(issue != null ? issue : "Manually flagged by Admin");
            bin.setStatus(BinStatus.MAINTENANCE);
        } else {
            // If unflagging: Clear the issue and RECALCULATE the status
            bin.setIssue(null);

            // Smart Recovery Logic
            double fill = (bin.getFillLevel() != null) ? bin.getFillLevel() : 0.0;
            if (fill >= 90) {
                bin.setStatus(BinStatus.CRITICAL);
            } else if (fill >= 70) {
                bin.setStatus(BinStatus.FULL);
            } else {
                bin.setStatus(BinStatus.NORMAL);
            }
        }

        bin.setLastUpdated(Instant.now());
        return binRepository.save(bin);
    }

    @Override
    public List<Bin> getFullBins(int threshold) {
        return binRepository.findByFillLevelGreaterThan(threshold);
    }

    @Override
    public Bin updateBinPrediction(String id, Integer predictedLevel, LocalDateTime targetTime) {
        Bin bin = getBinByBinId(id);

        bin.setPredictedFillLevel(predictedLevel);
        bin.setPredictionTargetTime(targetTime);

        return binRepository.save(bin); // This updates the existing document
    }
}