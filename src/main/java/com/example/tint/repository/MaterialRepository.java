package com.example.tint.repository;

import com.example.tint.domain.Material;
import com.example.tint.domain.MaterialName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface MaterialRepository extends JpaRepository<Material, Long> {

    //재료이름으로 찾기, materialName,
    //입고순서로 정렬하여 리스트로 가져온다. 재료의 동일한 이름은 여러개일수 있다.
    List<Material> findAllByMaterialName(MaterialName materialName);

}
