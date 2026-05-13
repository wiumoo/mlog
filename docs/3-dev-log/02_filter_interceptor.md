# 1. JWTfilter

## 1. JWTfilter구현목표

- 본 단계에서는 휴대폰 인증 기반 로그인 시스템과 JWT 인증 구조를 구축하는 것을 목표로 했다.
- 기존 세션 기반 인증이 아닌 Stateless 방식(JWT) 을 사용하여 확장성과 성능을 고려한 구조로 설계하였다

## 2. 전체 인증 흐름
1. 사용자 인증번호 요청
2. Redis에 인증번호 저장 (TTL)
3. 사용자 로그인 요청 (전화번호 + 인증번호)
4. 인증번호 검증
5. 사용자 조회 or 신규 생성
6. JWT 토큰 발급
7. 클라이언트가 JWT 저장
8. 이후 요청마다 Authorization 헤더로 JWT 전송
9. 서버에서 JWT 필터로 사용자 인증
10. ThreadLocal에 사용자 정보 저장
11. Controller에서 로그인 사용자 사용

## 3. 주요 구현 기능
### 3.1 인증번호 기반 로그인 (Redis)
인증번호 6자리 랜덤 생성
Redis에 저장 (TTL 5분)
실제 SMS 대신 서버 로그 출력
로그인 시 Redis 값 비교 후 삭제
설계 이유
DB를 사용하지 않고 Redis를 사용하는 이유:
- 빠른 조회 속도
- TTL 기반 자동 만료
- 인증 데이터의 일시적 특성에 적합
### 3.2 JWT 토큰 발급
userId → subject 저장
nickname → claim 저장
만료시간 설정
{
  "sub": "4",
  "nickname": "mlog_xxx",
  "iat": ...,
  "exp": ...
}
설계 이유
서버에서 세션을 유지하지 않고,
토큰 자체에 사용자 정보를 포함하여
Stateless 구조를 구현하기 위함

### 3.3 JwtAuthFilter 구현

모든 요청에서 JWT를 검사하는 필터 구현

동작 흐름
요청 → Authorization 헤더 확인
→ Bearer 토큰 추출
→ JWT 검증
→ userId 추출
→ UserHolder 저장
→ Controller 전달
→ 요청 종료 후 remove
핵심 코드 구조
try {
    // 인증 처리
} finally {
    UserHolder.removeUser();
}
중요한 포인트
ThreadLocal은 반드시 remove 필요
→ 스레드 재사용으로 인한 데이터 오염 방지
### 3.4 UserHolder (ThreadLocal)
private static final ThreadLocal<Long> tl = new ThreadLocal<>();
설계 이유
요청 단위로 사용자 정보를 공유하기 위해
Controller / Service 어디서든 접근 가능하도록 설계

### 3.5 /user/me API

현재 로그인 사용자 정보 조회 API

Long userId = UserHolder.getUser();
동작
JWT → userId 추출 → DB 조회 → 사용자 반환
## 4. 최종 아키텍처
Client -> JwtAuthFilter -> JwtUtils -> UserHolder(ThreadLocal) -> Controller -> Service -> Mapper -> DB

## 5. 핵심 배운 점
1. JWT 기반 Stateless 인증 구조 이해
2. Filter vs Interceptor 역할 차이
3. ThreadLocal 사용 시 반드시 remove 필요
4. Redis + JWT + Filter + ThreadLocal 기반의 인증 시스템을 직접 구현하였다.

# 2. LoginInterceptor

## 1. 구현 목적
이번 단계에서는 JWT 인증 필터 이후에 실행되는 `LoginInterceptor`를 구현했다.

기존에는 로그인 성공 시 JWT를 발급하고, 요청이 들어올 때 `JwtAuthFilter`에서 JWT를 파싱하여 현재 로그인 사용자를 `UserHolder`에 저장하는 구조까지 구현되어 있었다.

하지만 `JwtAuthFilter`만으로는 특정 API가 반드시 로그인한 사용자만 접근해야 하는지 판단하기 어렵다.

따라서 이번 단계에서는 `LoginInterceptor`를 추가하여 로그인 여부를 검사하고, 로그인하지 않은 사용자가 보호된 API에 접근하면 `401 Unauthorized`를 반환하도록 구현했다.

## 2. 왜 LoginInterceptor가 필요한가?

### 2.1 JwtAuthFilter의 역할

`JwtAuthFilter`의 역할은 요청 헤더에 JWT가 있는지 확인하고, JWT가 존재하면 토큰을 파싱하여 사용자 정보를 꺼낸 뒤 `UserHolder`에 저장하는 것이다.

즉, `JwtAuthFilter`는 로그인 여부를 강제로 검사하는 역할이 아니라, 요청에 포함된 인증 정보를 미리 준비하는 역할이다.

```text
JwtAuthFilter 역할

1. Authorization 헤더 확인
2. JWT 토큰 존재 여부 확인
3. 토큰이 있으면 파싱
4. 사용자 ID 조회
5. 사용자 정보를 UserHolder에 저장
6. 다음 필터 또는 Controller로 요청 전달
```

## 3. 전체 인증 흐름

이번 구현 이후 전체 인증 흐름은 다음과 같다.

1. 클라이언트가 API 요청
2. JwtAuthFilter 실행
3. Authorization 헤더에서 JWT 확인
4. JWT가 있으면 파싱
5. 사용자 정보를 UserHolder에 저장
6. LoginInterceptor 실행
7. UserHolder에 사용자가 있는지 확인
8. 사용자가 없으면 401 Unauthorized 반환
9. 사용자가 있으면 Controller 실행
10. 요청 처리 완료 후 UserHolder 정리

정리하면 다음과 같다.

Request -> JwtAuthFilter -> UserHolder 저장 -> LoginInterceptor -> Controller

# 3. 현재 인증 구조 요약

Client -> Request with Authorization Header -> JwtAuthFilter -> Parse JWT -> Save user to UserHolder -> LoginInterceptor -> Check UserHolder -> Controller

- 토큰이 없는 경우:
Client -> Request without Authorization Header -> JwtAuthFilter -> No user saved -> LoginInterceptor -> UserHolder is null -> 401 Unauthorized

- 토큰이 있는 경우:

Client -> Request with JWT -> JwtAuthFilter -> UserHolder saved -> LoginInterceptor -> User exists -> Controller executed

# 4. 이번 단계에서 배운 점

이번 구현을 통해 Filter와 Interceptor의 역할 차이를 이해했다.

JwtAuthFilter는 요청에서 인증 정보를 읽고 현재 사용자를 준비하는 역할을 한다.

LoginInterceptor는 실제로 로그인 여부를 검사하고 보호된 API 접근을 제어하는 역할을 한다.

처음에는 Filter에서 모든 인증 검사를 처리할 수도 있다고 생각했지만, 로그인 API와 공개 API까지 모두 막힐 수 있기 때문에 역할을 분리하는 것이 더 좋은 구조라는 것을 알게 되었다.

또한 ThreadLocal을 사용하는 UserHolder는 요청이 끝난 뒤 반드시 정리해야 하며, 그렇지 않으면 이전 요청의 사용자 정보가 남을 수 있다는 점도 중요하게 확인했다.

# 5. 변경한 파일
```
com.mlog
 ├── config
 │   ├── WebConfig
 │   └── WebMvcConfig
 ├── controller
 ├── dto
 ├── entity
 ├── filter
 │   └── JwtAuthFilter
 ├── interceptor
 │   └── LoginInterceptor
 ├── mapper
 ├── service
 └── utils
 ```