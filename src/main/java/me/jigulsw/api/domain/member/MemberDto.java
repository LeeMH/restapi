package me.jigulsw.api.domain.member;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

public class MemberDto {

    @Data
    @Accessors(chain = true)
    @NoArgsConstructor
    @ApiModel("Member-BasicInfo")
    public static class BasicInfo {
        private Long id;

        private String mobile;

        private String name;

        private String email;
    }

}
