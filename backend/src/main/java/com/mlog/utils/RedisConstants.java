package com.mlog.utils;

public class RedisConstants {

    public static final String LOGIN_CODE_KEY = "login:code:";
    public static final Long LOGIN_CODE_TTL = 5L;

    public static final String CACHE_SHOP_DETAIL_KEY = "shop:detail:";
    public static final Long CACHE_SHOP_DETAIL_TTL = 30L;

    public static final String LOCK_SHOP_DETAIL_KEY = "lock:shop:detail:";
    public static final Long LOCK_SHOP_DETAIL_TTL = 10L;

    public static final Long CACHE_NULL_TTL = 2L;

    public static final String BLOG_LIKED_KEY = "blog:liked:";
    public static final String BLOG_LIKED_RANK_KEY = "blog:liked:rank";

    public static final String FOLLOW_USER_KEY = "follow:user:";

}
