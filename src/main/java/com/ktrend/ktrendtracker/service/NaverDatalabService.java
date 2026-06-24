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
import java.util.HashMap;
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

            HttpHeaders headers = createHeaders();

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

    public List<String> getPopularKeywords(
            List<String> candidateKeywords,
            LocalDate startDate,
            LocalDate endDate,
            int limit
    ) {
        if (clientId.isBlank() || clientSecret.isBlank()) {
            return new ArrayList<>();
        }

        List<String> keywords = candidateKeywords.stream()
                .filter(keyword -> keyword != null && !keyword.isBlank())
                .map(String::trim)
                .distinct()
                .limit(5)
                .toList();

        if (keywords.isEmpty()) {
            return new ArrayList<>();
        }

        try {
            String apiUrl = "https://openapi.naver.com/v1/datalab/search";

            HttpHeaders headers = createHeaders();

            List<Map<String, Object>> keywordGroups = new ArrayList<>();

            for (String keyword : keywords) {
                keywordGroups.add(Map.of(
                        "groupName", keyword,
                        "keywords", List.of(keyword)
                ));
            }

            Map<String, Object> requestBody = Map.of(
                    "startDate", startDate.toString(),
                    "endDate", endDate.toString(),
                    "timeUnit", "date",
                    "keywordGroups", keywordGroups
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode results = root.path("results");

            Map<String, Double> scoreMap = new HashMap<>();

            for (JsonNode result : results) {
                String keywordName = result.path("title").asText();
                JsonNode dataArray = result.path("data");

                double totalScore = 0;
                int count = 0;

                for (JsonNode data : dataArray) {
                    totalScore += data.path("ratio").asDouble();
                    count++;
                }

                double averageScore = count == 0 ? 0 : totalScore / count;
                scoreMap.put(keywordName, averageScore);
            }

            System.out.println("네이버 데이터랩 인기 키워드 점수 = " + scoreMap);

            return scoreMap.entrySet().stream()
                    .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                    .limit(limit)
                    .map(Map.Entry::getKey)
                    .toList();

        } catch (Exception e) {
            System.out.println("네이버 데이터랩 인기 키워드 조회 실패");
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);
        return headers;
    }
}