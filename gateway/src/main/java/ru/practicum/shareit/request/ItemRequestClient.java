package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.PostItemRequestDto;

@Service
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> create(Integer userId, PostItemRequestDto itemRequest) {
        return post("", userId, itemRequest);
    }

    public ResponseEntity<Object> findAllByUserID(Integer userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> findAll(Integer userId, Integer from, Integer size) {
        String path = "/all" + "?from=" + from + "&size=" + size;
        return get(path, userId, null);
    }

    public ResponseEntity<Object> findById(Integer userId, Integer requestId) {
        return get("/" + requestId, userId);
    }
}