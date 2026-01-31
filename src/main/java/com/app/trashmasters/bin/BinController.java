package com.app.trashmasters.bin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.app.trashmasters.bin.model.Bin;

import java.util.List;

@RestController
@RequestMapping("/api/bins")
@CrossOrigin(origins = "http://localhost:3000")
public class BinController {

    private final BinService binService;

    @Autowired
    public BinController(BinService binService) {
        this.binService = binService;
    }

    // GET all bins
    @GetMapping
    public ResponseEntity<List<Bin>> getAllBins() {
        return ResponseEntity.ok(binService.getAllBins());
    }

    // POST - Create a new Bin
    @PostMapping
    public ResponseEntity<Bin> createBin(@RequestBody Bin bin) {
        return ResponseEntity.ok(binService.createBin(bin));
    }

    // PUT - Flag an Issue
    @PutMapping("/{id}/flag")
    public ResponseEntity<Bin> flagBinIssue(@PathVariable String id, @RequestBody String issue) {
        Bin updatedBin = binService.flagBinIssue(id, issue);
        return ResponseEntity.ok(updatedBin);
    }

    // GET - Get only full bins (Useful for your Routing later)
    @GetMapping("/full")
    public ResponseEntity<List<Bin>> getFullBins() {
        //TODO Hardcoding 70% as the threshold for now
        return ResponseEntity.ok(binService.getFullBins(70));
    }
}
