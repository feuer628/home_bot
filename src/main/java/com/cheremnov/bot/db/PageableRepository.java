package com.cheremnov.bot.db;

import com.cheremnov.bot.db.trusted_user.TrustedUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.stereotype.Repository;

@NoRepositoryBean
public interface PageableRepository<T> extends CrudRepository<T, Long> {

    Page<T> findAll(Pageable pageable);
}
