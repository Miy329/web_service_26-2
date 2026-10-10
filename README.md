# Backend Framework 실습 과제 (Spring Boot)

상품(Item) 데이터를 다루는 간단한 REST API 서버입니다.
데이터는 DB 없이 메모리(HashMap)에 저장하므로 서버를 껐다 켜면 초기화됩니다.

## 실행 방법

```
./gradlew bootRun
```

서버 주소: `http://localhost:8080`

## 1. API 목록 (8개)

기본 주소: `/api/v3/items`

| 메소드 | 주소 | 설명 | 응답 코드 |
|---|---|---|---|
| POST | `/api/v3/items` | 상품 1개 생성 | 201 |
| POST | `/api/v3/items/bulk` | 상품 여러 개 한 번에 생성 | 201 |
| GET | `/api/v3/items` | 전체 조회 | 200 |
| GET | `/api/v3/items/{id}` | 단건 조회 | 200, 404 |
| PUT | `/api/v3/items/{id}` | 상품 수정 | 200, 404 |
| PUT | `/api/v3/items/{id}/price` | 가격만 수정 | 200, 400, 404 |
| DELETE | `/api/v3/items/{id}` | 상품 1개 삭제 | 200, 404 |
| DELETE | `/api/v3/items` | 전체 삭제 | 200 |

### 추가 API (수업 예제 + 테스트용)

| 메소드 | 주소 | 설명 |
|---|---|---|
| GET | `/api/v3/items/search?keyword=&page=&size=` | 검색 (쿼리 파라미터) |
| POST | `/api/v3/items/with-header` | 헤더 값 확인 |
| GET | `/api/v3/items/error/500` | 500 응답 확인용 테스트 API |
| GET | `/api/v3/items/error/503` | 503 응답 확인용 테스트 API |

## 2. 응답 코드

| 코드 | 의미 | 발생 상황 |
|---|---|---|
| 200 | OK | 조회, 수정, 삭제 성공 |
| 201 | Created | 상품 생성 성공 |
| 400 | Bad Request | 가격이 없거나 음수 |
| 404 | Not Found | 없는 상품 id |
| 500 | Internal Server Error | 테스트용 API |
| 503 | Service Unavailable | 테스트용 API |

## 3. 응답 형식

모든 응답은 `status`, `data` 형태로 통일했습니다.
`makeBody()`(성공), `makeError()`(실패) 메소드로 만듭니다.

성공
```
{
  "status": "success",
  "data": { "id": 1, "name": "연필", "price": 500 }
}
```

실패
```
{
  "status": "error",
  "data": "상품을 찾을 수 없습니다."
}
```

## 4. 미들웨어

`LoggingInterceptor`(인터셉터)로 요청 로그를 출력합니다.
`WebConfig`에서 `/api/` 주소에 적용했습니다.

- 요청이 들어오면: 메소드와 주소 출력
- 요청이 끝나면: 응답 코드와 처리 시간(ms) 출력

```
[LoggingInterceptor] 요청 시작: GET /api/v3/items
[LoggingInterceptor] 요청 완료, status=200, 처리시간=2ms
```

## 5. 실행 및 테스트 화면

`screenshots` 폴더에 있습니다.

| 번호 | 테스트 내용 | 캡처 |
|---|---|---|
| 01 | POST 상품 1개 생성 (201) | [사진 보기](screenshots/%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202026-10-10%20034028.png) |
| 02 | POST 상품 일괄 생성 (201) | [사진 보기](screenshots/%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202026-10-10%20034257.png) |
| 03 | GET 전체 조회 (200) | [사진 보기](screenshots/%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202026-10-10%20034632.png) |
| 04 | GET 단건 조회 (200) | [사진 보기](screenshots/%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202026-10-10%20034725.png) |
| 05 | PUT 상품 수정 (200) | [사진 보기](screenshots/%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202026-10-10%20034833.png) |
| 06 | PUT 가격만 수정 (200) | [사진 보기](screenshots/%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202026-10-10%20034917.png) |
| 07 | DELETE 단건 삭제 (200) | [사진 보기](screenshots/%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202026-10-10%20035033.png) |
| 08 | DELETE 전체 삭제 (200) | [사진 보기](screenshots/%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202026-10-10%20035132.png) |
| 09 | GET 존재하지 않는 상품 (404) | [사진 보기](screenshots/%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202026-10-10%20035233.png) |
| 10 | PUT 잘못된 가격 (400) | [사진 보기](screenshots/%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202026-10-10%20035516.png) |
| 11 | 500 오류 응답 테스트 | [사진 보기](screenshots/%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202026-10-10%20035632.png) |
| 12 | 503 오류 응답 테스트 | [사진 보기](screenshots/%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202026-10-10%20035745.png) |
| 13 | 미들웨어 콘솔 로그 | [사진 보기](screenshots/%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202026-10-10%20040007.png) |
