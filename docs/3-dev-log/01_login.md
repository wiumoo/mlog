# Dev Log - Login Feature

## 1. 구현 기능

휴대폰 번호 기반 문자 인증 로그인 기능을 구현했다.

현재 실제 SMS 발송은 구현하지 않았고, 개발 단계에서는 인증번호를 서버 로그에 출력하는 방식으로 대체했다.

로그인 성공 시 JWT를 발급한다.

---

## 2. 최종 로그인 흐름

```text
1. 사용자가 휴대폰 번호로 인증번호 요청
2. 서버가 휴대폰 번호 형식 검증
3. 서버가 6자리 인증번호 생성
4. Redis에 인증번호 저장
5. 인증번호 TTL 5분 설정
6. 서버 로그에 인증번호 출력
7. 사용자가 휴대폰 번호와 인증번호로 로그인 요청
8. Redis에서 인증번호 조회
9. 인증번호 검증
10. 검증 성공 시 Redis 인증번호 삭제
11. DB에서 사용자 조회
12. 사용자가 없으면 신규 사용자 생성
13. JWT 발급
14. 로그인 응답 반환
```

---

## 3. 주요파일

```text
controller/UserController.java
service/IUserService.java
service/impl/UserServiceImpl.java
mapper/UserMapper.java
entity/User.java
dto/LoginRequest.java
dto/LoginResponse.java
dto/Result.java
utils/RegexUtils.java
utils/RedisConstants.java
utils/SystemConstants.java
utils/RandomUtils.java
utils/JwtUtils.java
```

---

## 4. 구현 내용
5.1 인증번호 요청 API
POST /user/code?phone=01012345678

처리 내용:

1. phone 검증
2. 6자리 인증번호 생성
3. Redis 저장
4. 로그 출력
5. 성공 응답 반환

Redis 저장 구조:

key: login:code:{phone}
value: 6자리 인증번호
ttl: 5분
5.2 로그인 API
POST /user/login

Request:
```
{
  "phone": "01012345678",
  "code": "123456"
}
```

처리 내용:

1. phone 검증
2. code 빈 값 검증
3. Redis에서 인증번호 조회
4. 인증번호 비교
5. 성공 시 Redis 인증번호 삭제
6. 사용자 조회
7. 사용자가 없으면 자동 회원가입
8. JWT 생성
9. 로그인 응답 반환

Response:
```
{
  "success": true,
  "data": {
    "userId": 4,
    "nickname": "mlog_e1a9e51c",
    "token": "jwt-token"
  }
}
```