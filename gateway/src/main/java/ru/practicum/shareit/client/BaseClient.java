package ru.practicum.shareit.client;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class BaseClient {
    protected final RestTemplate rest;
    private static final String BASE_URL = "http://localhost:9090";

    public BaseClient() {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        this.rest = new RestTemplate(factory);
    }

    protected ResponseEntity<Object> get(String path, Long userId) {
        return makeAndSendRequest(HttpMethod.GET, path, userId, null);
    }

    protected ResponseEntity<Object> post(String path, Long userId, Object body) {
        return makeAndSendRequest(HttpMethod.POST, path, userId, body);
    }

    protected ResponseEntity<Object> patch(String path, Long userId, @Nullable Object body) {
        return makeAndSendRequest(HttpMethod.PATCH, path, userId, body);
    }

    protected ResponseEntity<Object> delete(String path, Long userId) {
        return makeAndSendRequest(HttpMethod.DELETE, path, userId, null);
    }

    private ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path, Long userId, @Nullable Object body) {
        String url = BASE_URL + path;
        HttpEntity<Object> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));
        ResponseEntity<Object> serverResponse;
        try {
            serverResponse = rest.exchange(url, method, requestEntity, Object.class);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return prepareGatewayResponse(serverResponse);
    }

    private HttpHeaders defaultHeaders(Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        if (userId != null) {
            headers.set("X-Sharer-User-Id", String.valueOf(userId));
        }
        return headers;
    }

    private static ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        } else {
            ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());
            if (response.hasBody()) {
                responseBuilder.body(response.getBody());
            }
            return responseBuilder.build();
        }
    }
}