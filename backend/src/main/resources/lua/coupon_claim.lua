-- KEYS[1] = coupon stock key
-- KEYS[2] = coupon user set key
-- KEYS[3] = coupon event meta key
-- ARGV[1] = userId
-- ARGV[2] = current timestamp millis

local stockKey = KEYS[1]
local userKey = KEYS[2]
local eventKey = KEYS[3]

local userId = ARGV[1]
local now = tonumber(ARGV[2])

-- 1. 이벤트 정보 확인
local startTime = tonumber(redis.call('HGET', eventKey, 'startTime'))
local endTime = tonumber(redis.call('HGET', eventKey, 'endTime'))
local status = tonumber(redis.call('HGET', eventKey, 'status'))

if startTime == nil or endTime == nil or status == nil then
    return 4
end

-- 2. 이벤트 활성 상태 확인
if status ~= 1 then
    return 5
end

-- 3. 이벤트 시작 전 확인
if now < startTime then
    return 6
end

-- 4. 이벤트 종료 후 확인
if now > endTime then
    return 7
end

-- 5. 이미 쿠폰을 받은 유저인지 확인
if redis.call('SISMEMBER', userKey, userId) == 1 then
    return 2
end

-- 6. 재고 확인
local stock = tonumber(redis.call('GET', stockKey))

if stock == nil then
    return 3
end

if stock <= 0 then
    return 1
end

-- 7. 재고 차감
redis.call('DECR', stockKey)

-- 8. 유저 발급 기록 저장
redis.call('SADD', userKey, userId)

return 0