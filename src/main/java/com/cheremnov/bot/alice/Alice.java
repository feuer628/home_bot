package com.cheremnov.bot.alice;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Alice {
    VALERA("media_player.valera"),
    NADIA("media_player.nadia"),
    GOSTINNAIA("media_player.gostinnaia");

    private final String name;

}
