package com.example.tint.service;

import com.example.tint.domain.*;
import com.example.tint.repository.JobOrderRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class JobOrderService {

    private final JobOrderRepository jobOrderRepository;
    private final MaterialService materialService;

    //주문생성
    @Transactional
    public Long save(JobOrder jobOrder){

        //사용자가 주문한 제품과 수량을 가져온다
        int orderQuantity = jobOrder.getOrderQuantity();  //주문수량
        ProductName productName = jobOrder.getProductName();

        //재고가 충분한지 확인한다
        if(!materialService.isEnoughMaterial(productName, orderQuantity)){
            throw new IllegalStateException("재료가 부족합니다.");
        }

        //해당 제품에 필요한 원재료 목록을 조회한다.
        Map<MaterialName, Long> requiredMaterialStock = materialService.getRequiredMaterialStock(productName);
        log.info("requiredMaterialStock, 원자재 재고수량 {}", requiredMaterialStock);

        //원재료별로 실제 재고 차감
        for(Map.Entry<MaterialName, Long> entry : requiredMaterialStock.entrySet()) {
            MaterialName materialName = entry.getKey();
            //주문수량
            long requiredOrderQuantity = orderQuantity;

            log.info("entry - 원재료이름 {}  ", entry.getKey());
            log.info("entry-원자재 재고수량 {}", entry.getValue());
            log.info("orderQuantity 주문수량 {}", requiredOrderQuantity);


            //DB에서 해당 원재료 가져오기(여러 개 가져오기), 재고리스트조회한다.
            List<Material> materials = materialService.findMaterialName(materialName);

            if(materials.isEmpty()){
                throw new IllegalStateException(materialName + " 재료가 존재하지 않습니다");
            }

            //필요한 만큼 재료 차감(FIFO방식)
            //만약 red 10, red 50 => 오더는 30이야 =>10개에서 10개 빼고-> 다음 50개에서 210개 빼고
            for(Material material : materials) {
                if(requiredOrderQuantity <= 0) break;

                long materialQuantity = material.getMaterialQuantity();  //남은재고수량 10

                //남은 재고와 주문수량을 비교하여 작은값을 가져온다
                long minQuantity = Math.min(materialQuantity, requiredOrderQuantity); //작은값  10

                //남은 재고에서 - minQuantity를 빼고 다시 남은재고의 값을 수정한다.
                material.setMaterialQuantity((int) (materialQuantity-minQuantity)); //10-10

                log.info("차감된 원재료 : {}, 차감량 {}, 남은 주문 필요량 {}", materialName, minQuantity, requiredOrderQuantity);
                materialService.saveMaterial(material);

                //어떤 Material에서 얼마나 차감했는지 기록
                //JobOrder ↔ JobOrderMaterial 양방향 연관관계 설정
                JobOrderMaterial jm = new JobOrderMaterial();
                jm.setMaterial(material);
                jm.setDeductedQuantity((int) minQuantity);
                jobOrder.addJobOrderMaterial(jm);

                //주문 수량 업데이트
                //차감된 수량만큼 남은 주문 수량 갱신
                requiredOrderQuantity -= minQuantity;

            }

            if(requiredOrderQuantity > 0 ){
                throw new IllegalStateException(materialName + "재료가 부족하여 차감할 수 없습니다");
            }
        }

        //모든 차감이 정상적으로 끝났으면 주문을 DB에 저장
        jobOrderRepository.save(jobOrder);
        return jobOrder.getId();
    }

    // 주문 상태만 업데이트 (재고 차감 X)
    @Transactional
    public void updateOrderState(Long jobOrderId, JobOrderState state) {
        JobOrder jobOrder = findOneOrder(jobOrderId).orElseThrow(() -> new IllegalArgumentException("주문 없음"));
        jobOrder.setJobOrderState(state);
    }

    //단건조회
    public Optional<JobOrder> findOneOrder(Long jobOrderId){
        return jobOrderRepository.findById(jobOrderId);
    }

    //전체주문조회
    public List<JobOrder> findJobOrders(){
        return jobOrderRepository.findAll();
    }

    //주문삭제
    @Transactional
    public void deleteJobOrder(Long jobOrderId) {

        //1.주문조회
        JobOrder jobOrder = jobOrderRepository.findById(jobOrderId).orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다, ID" + jobOrderId));

        // 정확히 복구
        //JobOrderMaterial 리스트는 주문할 때 기록한 **"어떤 재고(Material)에서 얼마나 차감했는가"를 담고 있다
        for (JobOrderMaterial jm : jobOrder.getJobOrderMaterials()) {

            //복구할 Material 꺼내기 - 차감했던 재고를 그대로 다시 가져옴
            Material material = jm.getMaterial();
           log.info("기존에 있던 재고 {}",material.getMaterialQuantity());
            log.info("더할 재고{}", jm.getDeductedQuantity() );

            //예전에 차감했던 수량(deductedQuantity)만큼 더해서 원래대로 되돌림
            material.setMaterialQuantity(material.getMaterialQuantity() + jm.getDeductedQuantity());
            materialService.saveMaterial(material);
        }
        //주문삭제
        jobOrderRepository.deleteById(jobOrderId);
    }
}
