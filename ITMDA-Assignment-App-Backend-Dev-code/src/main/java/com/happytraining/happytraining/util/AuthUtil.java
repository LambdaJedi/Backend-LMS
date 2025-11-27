package com.happytraining.happytraining.util;

import com.google.firebase.auth.FirebaseToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class AuthUtil {
    private AuthUtil() {}

    public static String currentUid() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;
        Object principal = auth.getPrincipal();
        if (principal == null) return null;
        if (principal instanceof String) return (String) principal;
        if (principal instanceof FirebaseToken) return ((FirebaseToken) principal).getUid();
        return auth.getName();
    }
}

