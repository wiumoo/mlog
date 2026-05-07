# Shop Cache Design

## 1. 기능 이름

Redis Cache Aside 기반 가게 조회/검색 기능

---

## 2. 기능 목적

mlog 프로젝트에서 사용자가 메인 페이지에서 가게 리스트를 조회하거나 특정 가게 상세 정보를 조회할 때, 매번 MySQL에 직접 접근하지 않고 Redis를 먼저 조회하도록 설계한다.

이를 통해 반복 조회 요청에서 MySQL 부하를 줄이고, 응답 속도를 개선하는 것을 목표로 한다.

이 기능은 단순한 가게 조회 기능이 아니라, 면접에서 Redis 캐싱 전략과 데이터 일관성 문제를 설명하기 위한 핵심 기능이다.

---

## 3. 개발 배경

가게 리스트와 가게 상세 정보는 사용자가 자주 조회하는 데이터이다.

만약 모든 요청이 MySQL로 직접 전달된다면 트래픽이 증가할수록 DB 부하가 커진다.

특히 다음과 같은 요청은 반복 조회될 가능성이 높다.

```text
- 메인 페이지 가게 리스트 조회
- 카테고리별 가게 검색
- 특정 인기 가게 상세 조회
```

## 4. 핵심 설계 방향

이 기능에서는 Cache Aside Pattern을 사용한다.

조회 흐름은 다음과 같다.

1. 사용자가 가게 조회 API 요청
2. 서버가 Redis에서 캐시 데이터 조회
3. cache hit이면 Redis 데이터를 바로 반환
4. cache miss이면 MySQL에서 데이터 조회
5. MySQL 조회 결과를 Redis에 저장
6. 사용자에게 응답 반환

수정 흐름은 다음과 같다.

1. 관리자가 가게 정보 수정 요청
2. MySQL 데이터를 먼저 수정
3. MySQL transaction commit 성공
4. Redis cache 삭제
5. 다음 조회 요청에서 최신 DB 데이터를 Redis에 다시 저장

## 5. 적용할 Redis 캐싱 전략

이 기능에서는 다음 Redis 캐싱 전략을 적용한다.

1. Cache Aside Pattern
2. Null Value Caching
3. Random TTL
4. Redis Mutex Lock
5. DB Update 후 Cache Delete

각 전략의 목적은 다음과 같다.
```
전략	목적
Cache Aside Pattern	        Redis를 먼저 조회하고, 없을 때만 DB 조회
Null Value Caching	        존재하지 않는 데이터 반복 조회로 인한 DB 부하 방지
Random TTL	                여러 캐시가 동시에 만료되는 문제 완화
Redis Mutex Lock	        Hot Key 만료 시 다수 요청이 동시에 DB로 몰리는 문제 방지
DB Update 후 Cache Delete	 DB와 Redis 간 데이터 일관성 관리
```

## 6. 주요 API

초기 구현 대상 API는 다음과 같다.

GET /shop/search
GET /shop/{id}
PUT /shop/{id}

각 API의 목적은 다음과 같다.
```
API	목적
GET /shop/search	가게 리스트 및 검색 결과 조회
GET /shop/{id}	    특정 가게 상세 조회
PUT /shop/{id}	    가게 정보 수정 및 Redis cache 삭제 흐름 검증
```

## 7.최종 목표

이 기능의 최종 목표는 다음과 같다.

- 사용자가 가게 리스트를 조회할 수 있다.
- 사용자가 키워드/카테고리 조건으로 가게를 검색할 수 있다.
- 사용자가 가게 상세 정보를 조회할 수 있다.
- 조회 요청은 Redis를 먼저 확인한다.
- Redis cache miss 시 MySQL에서 조회 후 Redis에 저장한다.
- 존재하지 않는 데이터는 null cache로 짧게 저장한다.
- TTL에 random offset을 추가하여 cache avalanche를 완화한다.
- hot key cache miss 시 mutex lock으로 cache breakdown을 방지한다.
- 가게 정보 수정 시 DB update 후 Redis cache를 삭제한다.