package com.example.tint.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.tint.domain.PredictionRecord;

public interface PredictionRecordRepository extends JpaRepository<PredictionRecord, Long> {
}
