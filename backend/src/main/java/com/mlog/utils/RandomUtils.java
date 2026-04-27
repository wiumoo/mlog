package com.mlog.utils;

import java.util.Random;
import java.util.UUID;

public class RandomUtils {

    // Generate 6-digit verification code
    public static String randomCode6() {
        return String.valueOf(new Random().nextInt(900000) + 100000);
    }


    // Generate random string with 8 characters
    public static String randomString8() {
        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 8);
    }

}
