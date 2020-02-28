package me.jigulsw.api.domain.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ResponseStatus {
    SUCCESS("성공", "success", true),
    FAILURE("실패", "failure", true);

    @Getter
    private String desc;

    @Getter
    private String descEng;

    @Getter
    private boolean active;
}
