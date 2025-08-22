package com.cheremnov.bot.security;

import com.cheremnov.bot.db.trusted_user.TrustedUserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserChecker {

    private static final long SUPER_ADMIN = 410563696;

    private static Set<Long> trustedUserIds;

    @Autowired
    private TrustedUserRepository trustedUserRepository;

    @PostConstruct
    public void reset() {
        Set<Long> tmpTrustedUserIds = new HashSet<>();
        trustedUserRepository.findAll().forEach(trustedUser -> tmpTrustedUserIds.add(trustedUser.getId()));
        trustedUserIds = tmpTrustedUserIds;
    }

    public boolean isUserTrusted(long userId) {
        return userId == SUPER_ADMIN || trustedUserIds.contains(userId);
    }

}
