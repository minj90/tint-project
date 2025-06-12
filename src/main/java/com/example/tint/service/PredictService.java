package com.example.tint.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.example.tint.domain.PredictionOrderInput;
import com.example.tint.domain.PredictionRecord;
import com.example.tint.dto.PredictRequest;
import com.example.tint.dto.PredictResponse;
import com.example.tint.dto.PredictResult;
import com.example.tint.repository.PredictionRecordRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class PredictService {

    private final PredictionRecordRepository predictionRecordRepository;

//    예측을 위해 요청을 보낼 Flask API 엔드포인트 URL
    private final String FLASK_URL = "http://localhost:8000/predict";

    public PredictResult getPrediction(PredictRequest request) {

        // 1. Flask 예측 요청

        //1-1  RestTemplate 생성 - 스프링에서 HTTP 요청을 쉽게 보낼 수 있는 클래스 (클라이언트 역할)
        RestTemplate restTemplate = new RestTemplate();

        //1-2. 요청 헤더 설정 - Content-Type을 JSON으로 설정 → Flask는 JSON 형식의 데이터를 받는 것으로 기대하고 있음
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        //1-3. 요청 엔티티 생성 - 실제 보낼 요청 본문과 헤더를 함께 담은 HttpEntity 생성 → 이걸 통해 Flask로 전송
        HttpEntity<PredictRequest> entity = new HttpEntity<>(request, headers);

        //1-4. Flask API 호출
        ResponseEntity<PredictResponse> response = restTemplate.exchange(  //exchange() 메서드로 HTTP POST 요청을 보냄
                FLASK_URL,
                HttpMethod.POST,
                entity,
                PredictResponse.class   //응답을 PredictResponse.class 형태로 자동 변환, response.getBody()로 Flask가 준 예측 결과 꺼냄,
        );

        double prediction = response.getBody().getPrediction();   //Flask 응답(JSON)을 자바 객체(PredictResponse)로 바꾼 후 그 안에 있는 prediction 값을 꺼냄
        //flask 확인 => return jsonify({'prediction': float(y_pred)})
        // prediction이라는 이름으로 예측값을 줬음


        //2. 원본 Json저장 - 예측 요청(request)을 JSON 문자열로 직렬화(serialize) 해서 저장
        //2-1 ObjectMapper 생성 - 자바 객체 ↔ JSON 문자열 간 변환 담당
        ObjectMapper mapper = new ObjectMapper();
        String inputJson = null;
        try {
            inputJson = mapper.writeValueAsString(request);  //request 객체(PredictRequest)를 JSON 문자열로 바꿈
        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
            log.error("예측 처리 중 에러 발생", e); // 실무에서는 log 사용 권장
        }

        // 3. 예측 요청 정보와 예측 결과를 DB에 저장
        //3-1 PredictionRecord 라는 엔티티 객체 생성
        PredictionRecord record = new PredictionRecord();
        record.setInputData(inputJson);  //inputJson (예측 입력값의 JSON 문자열)을 저장
        record.setPrediction(prediction);  //Flask로부터 받은 예측 결과 값을 저장
        record.setPredictedAt(LocalDateTime.now());

        // 4. 입력값 1건씩 PredictionOrderInput으로 변환
        List<PredictionOrderInput> orderInputList = request.getOrders().stream().map(order -> {
            PredictionOrderInput input = new PredictionOrderInput();
            input.setDate(order.getDate());
            input.setQty(order.getQty());
            input.setMaterialName(order.getMaterialName());
            input.setRecord(record);
            return input;
        }).toList();

        //양방형 설정
        record.setOrderInputs(orderInputList);

        //5.저장
        predictionRecordRepository.save(record);

        //예측 날짜 계산
        LocalDate maxDate = request.getOrders().stream()
                .map(order -> LocalDate.parse(order.getDate()))  //날짜 String -> LocalDate
                .max(Comparator.naturalOrder()) // 가장 큰 날짜 (가장 미래), Comparator.naturalOrder => 오름차순 비교 (기본 정렬 방식)
                .orElse(LocalDate.now());  // 만약 없으면 오늘 날짜 사용

        LocalDate predictedDate = maxDate.plusDays(3);

        return new PredictResult(prediction, predictedDate);
    }
}

//Comparable을 구현한 클래스에 대해 자연스러운 순서(natural order) 로 정렬
//예를 들어:
//숫자: 작은 값 → 큰 값 (1, 2, 3, ...)
//문자열: 알파벳 순 (apple, banana, cherry)
//날짜(LocalDate): 오래된 날짜 → 최신 날짜