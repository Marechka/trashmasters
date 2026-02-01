package com.app.trashmasters.bin;


import com.app.trashmasters.bin.dto.BinCreateRequest;
import com.app.trashmasters.bin.model.Bin;

import java.time.LocalDateTime;
import java.util.List;

public interface BinService {
    List<Bin> getAllBins();
    Bin createBin(BinCreateRequest request);
    Bin updateBinPrediction(String id, Integer predictedLevel, LocalDateTime targetTime);
    Bin getBinById(String id); // Added this as a helper
    Bin flagBinIssue(String id, String issueDescription);
    List<Bin> getFullBins(int threshold); // Future-proofing for your route generator
}