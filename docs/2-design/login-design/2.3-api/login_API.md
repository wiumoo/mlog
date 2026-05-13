# login_API

## POST /api/auth/code

- Request
```
{
    "phone": "01012345678"
}
```
- Response
```
{
    "success": true,
    "message": "ok",
    "data": null
}
```

## POST /api/auth/login

- Request
```
{
  "phone": "01012345678",
  "code": "123456"
}
```
- Response
```
{
  "success": true,
  "message": "ok",
  "data": {
    "token": "jwt"
  }
}
```
