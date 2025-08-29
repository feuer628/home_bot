package com.cheremnov.bot.command.chess;

import com.cheremnov.bot.db.PageableRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends PageableRepository<GroupTour> {
}
