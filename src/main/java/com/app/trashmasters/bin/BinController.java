package com.app.trashmasters.bin;

import com.app.trashmasters.bin.dto.BinCreateRequest;
import com.app.trashmasters.bin.dto.BinFlagRequest;
import com.app.trashmasters.bin.dto.PredictionRequest;
import com.app.trashmasters.bin.model.Bin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/bins")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class BinController {

    private final BinService binService;

    @Autowired
    public BinController(BinService binService) {
        this.binService = binService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bin> getBinById(@PathVariable String id) {
        try {
            Bin bin = binService.getBinById(id);
            return ResponseEntity.ok(bin);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET all bins
    @GetMapping
    public ResponseEntity<List<Bin>> getAllBins() {
        return ResponseEntity.ok(binService.getAllBins());
    }

    // POST - Create a new Bin
    @PostMapping("/createBin")
    public ResponseEntity<Bin> createBin(@RequestBody BinCreateRequest request) {
        // Basic validation: Make sure they actually sent an ID and Depth
        if (request.getBinId() == null || request.getDepthCm() == null) {
            return ResponseEntity.badRequest().build();
        }

        Bin savedBin = binService.createBin(request);

        return new ResponseEntity<>(savedBin, HttpStatus.CREATED);
    }


    // PUT - Update ONLY the prediction for a specific bin
    @PutMapping("/{id}/prediction")
    public ResponseEntity<Bin> updatePrediction(
            @PathVariable String id,
            @RequestBody PredictionRequest request) {

        // Use the time provided, or default to 4 hours from now if missing
        LocalDateTime targetTime = (request.getTime() != null)
                ? request.getTime()
                : LocalDateTime.now().plusHours(4);

        return ResponseEntity.ok(
                binService.updateBinPrediction(id, request.getLevel(), targetTime)
        );
    }

    // PUT - Flag an Issue
    @PutMapping("/{id}/flag")
    public ResponseEntity<Bin> flagBinIssue(
            @PathVariable String id,
            @RequestBody BinFlagRequest request) {

        return ResponseEntity.ok(binService.setFlag(id, request.isFlagged(), request.getIssue()));
    }

    // GET - Get only full bins
    @GetMapping("/full")
    public ResponseEntity<List<Bin>> getFullBins() {
        //TODO Hardcoding 70% as the threshold for now
        return ResponseEntity.ok(binService.getFullBins(70));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Bin> updateBin(
            @PathVariable String id,
            @RequestBody BinCreateRequest request) {
        try {
            Bin updatedBin = binService.updateBin(id, request);
            return ResponseEntity.ok(updatedBin);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBin(@PathVariable String id) {
        try {
            binService.deleteBin(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
