package com.abdulhameed.foodieplan.utils;

import android.text.TextUtils;
import android.util.Patterns;

public class SignupValidator {
    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static boolean isValidUsername(String username) {
        return !TextUtils.isEmpty(username) && username.length() >= 3;
    }

    public static boolean isValidPassword(String password) {
        return !TextUtils.isEmpty(password) && password.length() >= 6;
    }

    public static boolean isValidPasswordConfirmation(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }
}