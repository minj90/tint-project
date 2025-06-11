package com.example.tint.service;

import com.example.tint.domain.Material;
import com.example.tint.domain.MaterialName;
import com.example.tint.domain.ProductName;
import com.example.tint.dto.JobOrderForm;
import com.example.tint.repository.MaterialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class MaterialService {

    private final MaterialRepository materialRepository;

    //저장
    @Transactional
    public void saveMaterial(Material material){
        materialRepository.save(material);
    }

    //하나찾기
    public Optional<Material> findOne(Long materialid) {
        return materialRepository.findById(materialid);
    }

    //모두 찾기
    public List<Material> findMaterialList() {
        return materialRepository.findAll();
    }


    //재료이름으로 찾기
    public List<Material> findMaterialName(MaterialName materialName) {
        return materialRepository.findAllByMaterialName(materialName);
    }

    //삭제하기
    @Transactional
    public void deleteMaterial(Long materialId){
        this.materialRepository.deleteById(materialId);
    }

    //제품별 필요한 원재료 매핑하기 | ProductName별로 필요한 재료 정리
    private static final Map<ProductName, List<MaterialName>> PRODUCT_NAME_LIST_MAP = Map.of(
          ProductName.PINKTINT, List.of(MaterialName.BASEDYE, MaterialName.PINK, MaterialName.INCENSE),
            ProductName.REDTINT, List.of(MaterialName.BASEDYE, MaterialName.RED, MaterialName.INCENSE),
            ProductName.GREENTINT, List.of(MaterialName.BASEDYE, MaterialName.GREEN, MaterialName.INCENSE)
    );

    //원재료수 총합구하기
    public Map<MaterialName, Long> getMaterialQuantities(){
        //DB에서 재료를 가져온다
        List<Material> materials = materialRepository.findAll();

        //재료 이름별로 그룹화하고 총합 계산
        Map<MaterialName, Long> materialNameLongMap = materials.stream().collect(Collectors.groupingBy(
                Material::getMaterialName,  //재료 이름 기준으로 그룹화
                Collectors.summingLong(Material::getMaterialQuantity)  //각 그룹의 총합 계산
        ));
        return materialNameLongMap;
    }

    //정적메소드 참조  |  클래스:메소드, 클래스 이름뒤에 ::기호를 붙이고 정적 메소드 이름을 기술한다.
    //getOrDefault()를 사용해 값이 없으면 기본값 0 반환


    //productName에 필요한 재료의 총합 가져오기
    public Map<MaterialName, Long> getRequiredMaterialStock(ProductName productName){
        //전체 원재료 수량 가져오기
        Map<MaterialName, Long> materialQuantities = getMaterialQuantities();

        //제품별 필요한 원재료 리스트 가져오기
        List<MaterialName> requiredMaterials = PRODUCT_NAME_LIST_MAP.getOrDefault(productName, List.of());

        // Map.getOrDefault(key, defaultValue)
        // 키(key)에 해당하는 값이 존재하면 반환하고, 없으면 기본값(defaultValue)을 반환하는 메서드

        log.info("원재료 리스트 {}", requiredMaterials);

        //필요한 원재료 수량만 추출
        Map<MaterialName, Long> materialNameLongMap = requiredMaterials.stream().collect(Collectors.toMap(
                materialName -> materialName,
                materialName -> materialQuantities.getOrDefault(materialName, 0L)
        ));
        return materialNameLongMap;
    }

    //주문수량과 비교해서 재고 체크, 주문을 넣을 때 현재 재고가 충분한지 검사한다.
    public boolean isEnoughMaterial(ProductName productName, int orderQuantiry) {
        //productName에 필요한 재료 수량 가져오기
        Map<MaterialName, Long> requiredMaterialStock = getRequiredMaterialStock(productName
        );

        //전체 원재료 수량 가져오기
        Map<MaterialName, Long> materialQuantities = getMaterialQuantities();

        //모든 원재료가 주문량을 충족하는지 확인
        return requiredMaterialStock.entrySet().stream().allMatch(entry -> materialQuantities.getOrDefault(entry.getKey(), 0L) >= orderQuantiry);

    }

}
