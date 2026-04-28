# Dev Log - Frontend & Backend Auth Integration

## 1. 작업 개요

이번 작업에서는 기존에 백엔드에서 구현해둔 휴대폰 인증 로그인 기능을 프론트엔드와 실제로 연결했다.

이전 단계까지는 백엔드에서 다음 기능들이 이미 구현되어 있었다.

- 휴대폰 번호 기반 인증번호 생성
- Redis에 인증번호 저장
- 인증번호 TTL 설정
- 인증번호 검증
- 신규 유저 자동 생성
- JWT 발급
- JwtAuthFilter를 통한 토큰 검증
- UserHolder(ThreadLocal)를 통한 현재 로그인 유저 저장
- LoginInterceptor를 통한 인증 필요 API 보호
- `/user/me` API를 통한 현재 로그인 유저 조회

이번 단계에서는 React 프론트엔드에서 실제로 백엔드 API를 호출하고, 로그인 성공 후 JWT를 저장한 뒤 인증이 필요한 `/user/me` API까지 호출하는 흐름을 완성했다.

---

## 2. 최종 완성된 로그인 흐름

```text
1. 사용자가 프론트 로그인 화면에 접속한다.
2. 휴대폰 번호를 입력한다.
3. Send Code 버튼을 클릭한다.
4. 프론트에서 POST /user/code 요청을 보낸다.
5. 백엔드는 인증번호를 생성한다.
6. 인증번호를 Redis에 저장한다.
7. 개발 단계에서는 인증번호를 백엔드 콘솔 로그에 출력한다.
8. 사용자가 인증번호를 입력한다.
9. Login 버튼을 클릭한다.
10. 프론트에서 POST /user/login 요청을 보낸다.
11. 백엔드는 Redis에서 인증번호를 조회한다.
12. 인증번호를 검증한다.
13. 검증 성공 시 Redis에서 인증번호를 삭제한다.
14. DB에서 휴대폰 번호로 유저를 조회한다.
15. 기존 유저가 있으면 해당 유저를 사용한다.
16. 기존 유저가 없으면 신규 유저를 생성한다.
17. 백엔드는 JWT를 발급한다.
18. 프론트는 응답으로 받은 token, userId, nickname을 localStorage에 저장한다.
19. 로그인 성공 후 /me 화면으로 이동한다.
20. ProfilePage에서 GET /user/me 요청을 보낸다.
21. axios request interceptor가 Authorization 헤더에 Bearer token을 자동 추가한다.
22. 백엔드 JwtAuthFilter가 Authorization 헤더에서 JWT를 꺼낸다.
23. JwtAuthFilter가 JWT를 검증하고 userId를 추출한다.
24. 추출한 userId를 UserHolder(ThreadLocal)에 저장한다.
25. LoginInterceptor가 UserHolder에 로그인 유저가 있는지 확인한다.
26. /user/me 컨트롤러에서 현재 userId로 DB를 조회한다.
27. 프론트 프로필 화면에 유저 정보가 표시된다.
```

## 3. 구현한 프론트엔드 기능
```
frontend/src/
├─ api/
│  ├─ request.ts
│  └─ auth.ts
├─ pages/
│  ├─ LoginPage.tsx
│  ├─ WelcomePage.tsx
│  └─ ProfilePage.tsx
├─ App.tsx
├─ main.tsx
└─ styles.css
```
### 3.2 로그인 UI 구현
초기 화면은 로그인 페이지로 구성했다.

구현한 화면 흐름은 다음과 같다.
```
/          → LoginPage
/welcome   → WelcomePage
/me        → ProfilePage
```
로그인 화면에서는 다음 요소를 구현했다.

뒤로가기 버튼
Help 버튼
휴대폰 번호 입력창
인증번호 입력창
Send Code 버튼
Login 버튼
약관 동의 체크 버튼
소셜 로그인 형태의 UI 버튼

초기에는 단순히 전화번호만 입력하면 /me로 이동하는 가짜 로그인 구조였지만, 이후 실제 백엔드 API를 호출하는 구조로 변경했다.

## 4. 백엔드 수정 내용

### 4.1 CORS 설정 추가

프론트는 다음 주소에서 실행된다.

http://localhost:5173

백엔드는 다음 주소에서 실행된다.

http://localhost:8080

포트가 다르기 때문에 브라우저에서는 CORS 문제가 발생할 수 있다.

이를 해결하기 위해 WebMvcConfig에 CORS 설정을 추가했다.

@Override
public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
            .allowedOrigins("http://localhost:5173")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true);
}

### 4.2 /user/code 요청 방식 수정
초기 백엔드 코드는 인증번호 요청을 다음 방식으로 받고 있었다.

@PostMapping("code")
public Result sendCode(@RequestParam("phone") String phone) {
    return userService.sendCode(phone);
}

이 방식은 프론트에서 다음처럼 요청해야 한다.

POST /user/code?phone=01012345678

하지만 프론트에서는 JSON 방식으로 요청하고 있었다.

{
  "phone": "01012345678"
}

그래서 백엔드에서 다음과 같은 오류가 발생했다.

MissingServletRequestParameterException:
Required request parameter 'phone' for method parameter type String is not present

해결 방법으로 @RequestBody SendCodeRequest를 사용하도록 수정했다.

@PostMapping("/code")
public Result sendCode(@RequestBody SendCodeRequest request) {
    return userService.sendCode(request.getPhone());
}

이렇게 수정하면서 프론트와 백엔드의 요청 방식이 JSON 기준으로 통일되었다.
### 4.3 JwtAuthFilter 수정
프론트에서는 JWT를 다음 형태로 보낸다.

Authorization: Bearer {token}

따라서 백엔드 필터에서는 Bearer 부분을 제거한 뒤 순수 JWT만 파싱해야 한다.

필터의 핵심 흐름은 다음과 같다.

1. Authorization 헤더 조회
2. Bearer 로 시작하는지 확인
3. Bearer 이후의 token 부분만 추출
4. JwtUtils로 userId 추출
5. UserHolder에 userId 저장
6. 다음 필터 또는 인터셉터로 진행
7. 요청 종료 후 UserHolder.removeUser() 호출

### 4.4 LoginInterceptor OPTIONS 요청 처리

CORS 환경에서는 브라우저가 실제 요청 전에 OPTIONS preflight 요청을 보낼 수 있다.

이 요청이 인터셉터에서 막히면 실제 API 요청이 실패할 수 있다.

따라서 LoginInterceptor에서 OPTIONS 요청은 통과시키는 처리를 추가했다.

if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
    return true;
}

## 5. 현재 완료 상태
현재 완료 상태

현재 로그인 관련 기능은 포트폴리오 1차 버전 기준으로 완료되었다.

완료된 범위:

1. 프론트 로그인 UI 구현
2. 인증번호 요청 버튼 구현
3. 백엔드 /user/code 연동
4. Redis 인증번호 저장
5. 인증번호 입력 후 로그인
6. 백엔드 /user/login 연동
7. 신규 유저 생성 또는 기존 유저 조회
8. JWT 발급
9. localStorage에 token 저장
10. axios interceptor로 Authorization 헤더 자동 추가
11. JwtAuthFilter로 JWT 인증
12. UserHolder에 현재 유저 저장
13. LoginInterceptor로 보호 API 접근 제어
14. /user/me로 현재 유저 조회
15. ProfilePage에 현재 유저 정보 표시
16. 로그아웃 처리

## 6. 앞으로의 개선 사항

실서비스 수준으로 가려면 다음 기능들이 추가로 필요하다.

1. 실제 SMS 발송 연동
2. 인증번호 재요청 제한
3. 인증번호 입력 실패 횟수 제한
4. JWT 만료 시 자동 로그아웃 처리
5. Refresh Token 도입
6. 로그인 실패 에러 메시지 세분화
7. 프론트 라우트 보호 처리
8. token 만료 시 /login 자동 이동
9. 비밀번호 로그인 또는 소셜 로그인
10. 프로필 수정 기능