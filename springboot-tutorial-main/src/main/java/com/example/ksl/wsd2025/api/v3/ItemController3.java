package com.example.ksl.wsd2025.api.v3;

import com.example.ksl.wsd2025.api.dto.ItemDto;
import com.example.ksl.wsd2025.api.request.ItemCreateRequest;
import com.example.ksl.wsd2025.api.request.ItemUpdateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v3/items")
public class ItemController3 {

    // 메모리용 임시 DB (예제용)
    private final Map<Long, ItemDto> store = new HashMap<>();
    private long sequence = 1L;

    private Map<String, Object> makeBody(Object data) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", "success");
        body.put("data", data);
        return body;
    }
    //오류 응답용
    private Map<String, Object> makeError(String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", "error");
        body.put("data", message);
        return body;
    }

    // 1) GET: 전체 조회
    @GetMapping
    public Map<String, Object> getItems() {
        return makeBody(new ArrayList<>(store.values()));
    }

    // 2) GET: 단건 조회 (PathVariable 사용)
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getItem(@PathVariable Long id) {
        ItemDto item = store.get(id);
        if (item == null) {
            return ResponseEntity.status(404)
                    .body(makeError("상품을 찾을 수 없습니다."));
        }
        return ResponseEntity.ok(makeBody(item)); //200 반환
    }

    // 1) POST: 생성
    @PostMapping
    public ResponseEntity<Map<String, Object>> createItem(
            @RequestBody ItemCreateRequest request) {
        ItemDto item = new ItemDto();
        item.setId(sequence++);
        item.setName(request.getName());
        item.setPrice(request.getPrice());

        store.put(item.getId(), item);

        return ResponseEntity.status(201).body(makeBody(item));
    }

    // 2) POST: 일괄 생성

    @PostMapping("/bulk")
    public ResponseEntity<Map<String, Object>> createItems(
            @RequestBody List<ItemCreateRequest> requests
    ) {
        List<ItemDto> result = new ArrayList<>();

        for (ItemCreateRequest request : requests) {
            ItemDto item = new ItemDto();

            item.setId(sequence++);
            item.setName(request.getName());
            item.setPrice(request.getPrice());

            store.put(item.getId(), item);
            result.add(item);
        }

        return ResponseEntity.status(201).body(makeBody(result));
    }

    // 1) PUT: 수정
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateItem(
            @PathVariable Long id,
            @RequestBody ItemUpdateRequest request
    ) {
        ItemDto item = store.get(id);
        if (item == null) {
            return ResponseEntity.status(404)
                    .body(makeError("상품을 찾을 수 없습니다."));
        }

        if (request.getName() != null) {
            item.setName(request.getName());
        }
        if (request.getPrice() != null) {
            item.setPrice(request.getPrice());
        }

        return ResponseEntity.ok(makeBody(item));
    }

    // 2) PUT: 상품 가격만 수정
    @PutMapping("/{id}/price")
    public ResponseEntity<Map<String, Object>> updatePrice(
            @PathVariable Long id,
            @RequestBody ItemUpdateRequest request
    ) {
        ItemDto item = store.get(id);

        if (item == null) {
            return ResponseEntity.status(404)
                    .body(makeError("상품을 찾을 수 없습니다."));
        }

        if (request.getPrice() == null || request.getPrice() < 0) {
            return ResponseEntity.status(400)
                    .body(makeError("가격은 0 이상의 숫자여야 합니다."));
        }

        item.setPrice(request.getPrice());

        return ResponseEntity.ok(makeBody(item));
    }

    // 1) DELETE: 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteItem(@PathVariable Long id) {
        ItemDto removed = store.remove(id);
        if (removed == null) {
            return ResponseEntity.status(404)
                    .body(makeError("삭제할 상품이 없습니다."));
        }
        return ResponseEntity.ok(makeBody("상품 삭제 완료"));
    }

    //2) DELETE: 전체 삭제
    @DeleteMapping
    public ResponseEntity<Map<String, Object>> deleteAll() {
        store.clear();

        return ResponseEntity.ok(makeBody("전체 상품 삭제 완료"));
    }

    // /api/items/search?keyword=abc&page=1&size=10
    @GetMapping("/search")
    public Map<String, Object> searchItems(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        // 예제: keyword 포함된 name만 필터링
        List<ItemDto> result = store.values().stream()
                .filter(item -> keyword == null ||
                        item.getName().contains(keyword))
                .skip((long) page * size)
                .limit(size)
                .toList();

        return makeBody(result);
    }

    @PostMapping("/with-header")
    public ResponseEntity<Map<String, Object>> createItemWithHeader(
            @RequestBody ItemCreateRequest request,
            @RequestHeader(value = "X-USER-ID", required = false) String userId,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        StringBuilder sb = new StringBuilder();
        sb.append("요청한 아이템: ").append(request.getName())
                .append(", price=").append(request.getPrice()).append("\n")
                .append("X-USER-ID: ").append(userId).append("\n")
                .append("Authorization: ").append(authorization);

        return ResponseEntity.ok(makeBody(sb.toString()));
    }
// 500 오류 응답
    @GetMapping("/error/500")
    public ResponseEntity<Map<String, Object>> test500() {
        return ResponseEntity.status(500)
                .body(makeError("서버 내부 처리 중 오류가 발생했습니다."));
    }
//503 오류 응답
    @GetMapping("/error/503")
    public ResponseEntity<Map<String, Object>> test503() {
        return ResponseEntity.status(503)
                .body(makeError("현재 서비스를 일시적으로 이용할 수 없습니다."));
    }
}
