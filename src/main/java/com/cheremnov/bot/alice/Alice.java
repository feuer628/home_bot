package com.cheremnov.bot.alice;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Alice {
    VALERA("media_player.valera", "Валера"),
    NADIA("media_player.nadia", "Надя"),
    GOSTINNAIA("media_player.gostinnaia", "Гостиная");

    private final String id;
    private final String name;

}
