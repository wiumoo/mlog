import request from "./request";

export interface ApiResult<T> {
  success: boolean;
  data: T;
  errorMsg?: string;
}

export interface SendCodeRequest {
  phone: string;
}

export interface LoginRequest {
  phone: string;
  code: string;
}

export interface LoginResponse {
  userId: number;
  nickname: string;
  token: string;
}

export function sendCodeApi(data: SendCodeRequest) {
  return request.post<ApiResult<null>>("/user/code", data);
}

export function loginApi(data: LoginRequest) {
  return request.post<ApiResult<LoginResponse>>("/user/login", data);
}

export function getMeApi() {
  return request.get<ApiResult<any>>("/user/me");
}