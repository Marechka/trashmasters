package com.app.trashmasters.bin;

import com.app.trashmasters.bin.dto.BinCreateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.app.trashmasters.bin.model.Bin;


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
        bin.setLocationName(request.getLocationName());
        bin.setLatitude(request.getLatitude());
        bin.setLongitude(request.getLongitude());
        bin.setFillLevel(request.getFillLevel());
        bin.setSensorId(request.getSensorId());

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
    public Bin getBinById(String id) {
        return binRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bin not found with id: " + id));
    }

    @Override
    public Bin flagBinIssue(String id, String issueDescription) {
        Bin bin = getBinById(id);

        bin.setFlagged(true);
        bin.setIssueDescription(issueDescription);

        return binRepository.save(bin);
    }

    @Override
    public List<Bin> getFullBins(int threshold) {
        return binRepository.findByFillLevelGreaterThan(threshold);
    }

    @Override
    public Bin updateBinPrediction(String id, Integer predictedLevel, LocalDateTime targetTime) {
        Bin bin = getBinById(id); // Reuse our helper to find it first

        bin.setPredictedFillLevel(predictedLevel);
        bin.setPredictionTargetTime(targetTime);

        return binRepository.save(bin); // This updates the existing document
    }
}