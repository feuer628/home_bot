package com.cheremnov.bot.security;

import com.cheremnov.bot.db.trusted_user.TrustedUserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@DependsOn("dataMigration")
public class UserChecker {

    /** Ключи супер-администраторов вида "tg:410563696" / "mx:123456". */
    private final Set<String> superAdmins;

    private static Set<String> trustedUserIds;

    @Autowired
    private TrustedUserRepository trustedUserRepository;

    public UserChecker(@Value("${bot.super-admins:tg:410563696}") List<String> superAdmins) {
        this.superAdmins = new HashSet<>();
        for (String admin : superAdmins) {
            if (admin != null && !admin.isBlank()) {
                this.superAdmins.add(admin.trim());
            }
        }
    }

    @PostConstruct
    public void reset() {
        Set<String> tmpTrustedUserIds = new HashSet<>();
        trustedUserRepository.findAll().forEach(trustedUser -> tmpTrustedUserIds.add(trustedUser.getId()));
        trustedUserIds = tmpTrustedUserIds;
    }

    public boolean isUserTrusted(String userKey) {
        return superAdmins.contains(userKey) || trustedUserIds.contains(userKey);
    }

}