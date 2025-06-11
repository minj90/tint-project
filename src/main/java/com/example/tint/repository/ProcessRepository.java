package com.example.tint.repository;

import com.example.tint.domain.PinkProcess;
import com.example.tint.domain.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProcessRepository extends JpaRepository<PinkProcess, Long> {

    //jobOrderId로 찾기
    List<PinkProcess> findAllByJobOrder_Id(Long jobOrderId);

    //lotNumber로 찾기
    List<PinkProcess> findAllByLotNumber(String lotNumber);

    //lotNumber&공정으로 찾기
    List<PinkProcess> findAllByLotNumberAndStep(String lotNumber, Long step);

    // 공정중 잡오더를 기준으로 오름차순, 상태값으로 찾기
    Optional<PinkProcess> findFirstByStatusOrderByJobOrderIdAsc(Status status);

    //날짜를 기준으로, h2서버에서는 :date지원하지 않는다.
//    @Query("SELECT p From PinkProcess p where DATE(p.processDate) = :date ORDER BY p.step, p.lotNumber")
//    List<PinkProcess> findAllByProcessDate(@Param("date") LocalDate date);

    //날짜를 기준으로 하루범위조회
    @Query("SELECT p FROM PinkProcess p WHERE p.processDate BETWEEN :start AND :end ORDER BY p.step, p.lotNumber")
    List<PinkProcess> findAllByProcessDateBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);


    long countByJobOrderIdAndStep(Long jobOrderId, int step);

}
