package com.example.tint.dto;

import com.example.tint.domain.PinkProcess;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class ProcessDataDto {

    private List<PinkProcess> processes;

    Map<Integer, Long> countByStep;

    Map<Integer, Long> ngCountByStep;

    Map<Integer, Long> okCountByStep;

    private long productCount;

    private int totalCount;


}
