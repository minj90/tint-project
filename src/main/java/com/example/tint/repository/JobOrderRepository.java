package com.example.tint.repository;

import com.example.tint.domain.JobOrder;
import com.example.tint.domain.JobOrderState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JobOrderRepository extends JpaRepository<JobOrder, Long> {

    List<JobOrder> findByJobOrderState(JobOrderState state);

    //주문리스트 내림차순
    Optional<JobOrder> findTopByOrderByIdDesc();
}
