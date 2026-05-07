# API Design

## API 목록

| Method | URL | 설명 |
|---|---|---|
| GET | /shop/search | 가게 리스트/검색 |
| GET | /shop/{id} | 가게 상세 조회 |
| PUT | /shop/{id} | 가게 정보 수정 |

## 인증
- GET /shop/search: 비로그인 가능
- GET /shop/{id}: 비로그인 가능
- PUT /shop/{id}: 로그인 필요