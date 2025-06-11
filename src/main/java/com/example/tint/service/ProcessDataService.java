package com.example.tint.service;

import com.example.tint.domain.PinkProcess;
import com.example.tint.domain.Status;
import com.example.tint.dto.ProcessDataDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProcessDataService {

    private final ProcessService processService;

    //오더기준
    public ProcessDataDto getProcessData(Long jobOrderId){
        List<PinkProcess> processes = processService.findAllJobOrder(jobOrderId);
        return analyzeProcesses(processes);
    }

    //날짜기준
    public ProcessDataDto getProcessDataByDate(LocalDate date) {
        List<PinkProcess> processes = processService.findAllByDate(date);
        return analyzeProcesses(processes);
    }

    //공통로직분리
    private ProcessDataDto analyzeProcesses(List<PinkProcess> processes) {
        //차트를 위한 추가
        //공정별 통계, Map<Integer, Long> 형태로 저장됨
        Map<Integer, Long> countByStep = processes.stream()
                .collect(Collectors.groupingBy(PinkProcess::getStep, Collectors.counting()));
        log.info("공정별 개수 {}", countByStep);

        //공정별 에러 {1=2, 2=1, 3=0}
        Map<Integer, Long> ngCountByStep = processes.stream()
                .filter(p-> Status.NG.equals(p.getStatus()))
                .collect(Collectors.groupingBy(PinkProcess::getStep, Collectors.counting()));
        log.info("공정별 에러개수 {}", ngCountByStep);

        //공졍별 OK
        Map<Integer, Long> okCountByStep = processes.stream()
                .filter(p-> Status.OK.equals(p.getStatus()))
                .collect(Collectors.groupingBy(PinkProcess::getStep, Collectors.counting()));
        log.info("공정별 ok개수 {}", okCountByStep);

        //lotNumber별 모든 공정이 "OK"인지를 확인하여 공정 개수 계산 - 로트번호별 상태값을 가져온다
        //lotNumber 별로 모든 공정 상태를 Set<String>으로 저장
        Map<String, Set<Status>> lotStatusMap =
                processes.stream()
                        .collect(Collectors.groupingBy(p-> lotNumberGroup(p.getLotNumber()) ,
                                Collectors.mapping(PinkProcess::getStatus, Collectors.toSet())));

        log.info("lot별 ? {}", lotStatusMap);

        //모든 상태가 "OK"인 LotNumber만 선택하여 개수 계산
        //Set<String>의 크기가 1이고, 그 값이 "OK"라면 해당 lotNumber는 완제품
        long productCount = lotStatusMap.values().stream()
                .filter(statuses -> statuses.size() == 1 && statuses.contains(Status.OK)).count();

        log.info("lot별 모두 ok인 : 완제품 개수 {}", productCount);

        //공정 전체 개수, 주문수
        int totalCount = (processes.size() / 3);  //주문수
        log.info("주문수 {} ", totalCount);

        // DTO에 담아서 반환
        return new ProcessDataDto(processes, countByStep, ngCountByStep, okCountByStep,productCount,totalCount);
    }

    //로트번호에서 공통 부분만 추출하는 함수, PINKTINT_1_001_01_20250308
    private String lotNumberGroup(String lotNumber) {
        String[] parts = lotNumber.split("_");
        if(parts.length >= 3) {
            return parts[0] + "_" + parts[1] + "_" + parts[2];
        }
        return lotNumber;
    };

}
