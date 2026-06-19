package com.ktrend.ktrendtracker.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ktrend.ktrendtracker.entity.Keyword;
import com.ktrend.ktrendtracker.entity.TrendData;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NaverDatalabService {

    @Value("${naver.client-id:}")
    private String clientId;

    @Value("${naver.client-secret:}")
    private String clientSecret;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<TrendData> getTrendData(Keyword keyword, LocalDate startDate, LocalDate endDate) {
        List<TrendData> trendDataList = new ArrayList<>();

        if (clientId.isBlank() || clientSecret.isBlank()) {
            return trendDataList;
        }

        try {
            String apiUrl = "https://openapi.naver.com/v1/datalab/search";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Naver-Client-Id", clientId);
            headers.set("X-Naver-Client-Secret", clientSecret);

            Map<String, Object> requestBody = Map.of(
                    "startDate", startDate.toString(),
                    "endDate", endDate.toString(),
                    "timeUnit", "date",
                    "keywordGroups", List.of(
                            Map.of(
                                    "groupName", keyword.getKeywordName(),
                                    "keywords", List.of(keyword.getKeywordName())
                            )
                    )
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    request,
                    String.class
            );
            System.out.println("네이버 데이터랩 API 응답 성공");
            System.out.println(response.getBody());

            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode results = root.path("results");

            if (results.isEmpty()) {
                return trendDataList;
            }

            JsonNode dataArray = results.get(0).path("data");

            for (JsonNode data : dataArray) {
                LocalDate trendDate = LocalDate.parse(data.path("period").asText());
                int interestValue = (int) Math.round(data.path("ratio").asDouble());

                trendDataList.add(new TrendData(
                        keyword,
                        trendDate,
                        interestValue,
                        "NAVER_DATALAB"
                ));
            }

        } catch (Exception e) {
            System.out.println("네이버 데이터랩 API 호출 실패");
            e.printStackTrace();
            return new ArrayList<>();
        }

        return trendDataList;
    }
}