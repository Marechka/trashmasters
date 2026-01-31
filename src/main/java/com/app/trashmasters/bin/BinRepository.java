package com.app.trashmasters.bin;


import com.app.trashmasters.bin.model.Bin;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BinRepository extends MongoRepository<Bin, String> {
    // We need this to find only the bins that NEED pickup (> 70% full)
    List<Bin> findByFillLevelGreaterThan(int level);
}