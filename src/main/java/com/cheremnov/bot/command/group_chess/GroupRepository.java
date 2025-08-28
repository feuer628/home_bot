package com.cheremnov.bot.command.group_chess;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends CrudRepository<GroupTour, Long> {

    Page<GroupTour> findAll(Pageable pageable);
}
