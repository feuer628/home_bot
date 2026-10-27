package com.cheremnov.bot.db.trusted_user;

import com.cheremnov.bot.db.PageableRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrustedUserRepository extends PageableRepository<TrustedUser, String> {


}