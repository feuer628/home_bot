package com.cheremnov.bot.command.user;

import com.cheremnov.bot.db.user.TrustedUser;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;

@Data
public class TrustedUserInfoModel {
    private Long userId;
    private int pNum;
}
