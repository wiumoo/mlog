package com.mlog.utils;

import java.util.regex.Pattern;

public class RegexUtils {

    //1. phone number
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^010\\d{8}$");

    public static boolean isPhoneInvalid(String phone) {
        return phone == null || !PHONE_PATTERN.matcher(phone).matches();
    }

}
