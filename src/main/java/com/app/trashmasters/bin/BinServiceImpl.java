package com.app.trashmasters.bin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.app.trashmasters.bin.model.Bin;


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
    public Bin createBin(Bin bin) {
        // TODO could add logic here ("Check if sensorId is unique")
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
}