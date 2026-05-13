# Redis Design

## Redis Key

- shop:detail:{shopId}
- shop:search:v{version}:kw:{keyword}:cat:{category}:region:{region}:page:{page}:size:{size}
- shop:search:version
- lock:shop:detail:{shopId}

## TTL
- shop detail: 30분 + random 0~10분
- shop search: 5분 + random 0~5분
- null cache: 2분
- lock: 10초

## 문제 대응
- Cache Penetration: null value caching
- Cache Avalanche: random TTL
- Cache Breakdown: Redis mutex lock

## 데이터 일관성
DB update → transaction commit → Redis cache delete