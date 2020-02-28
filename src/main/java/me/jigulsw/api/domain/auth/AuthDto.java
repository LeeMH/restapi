package me.jigulsw.api.domain.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

public class AuthDto {
    @Data
    @Accessors(chain = true)
    @NoArgsConstructor
    @ApiModel("Auth-MemberInfo")
    public static class MemberInfo {
        private Long id;

        private String mobile;

        private String name;

        private String email;
    }

    @Data
    @Accessors(chain = true)
    @NoArgsConstructor
    @ApiModel("Auth-MobileForm")
    public static class MobileForm {
        @ApiModelProperty(value = "휴대폰번호", notes = "숫자아닌값은 서버에서 replace 처리함", example = "01027058989", required = true)
        @NotEmpty(message = "전화번호는 필수 입니다.")
        private String mobile;

        public String getMobile() {
            return StringUtils.replaceAll(mobile, "[^0-9]", "");
        }
    }

    @Data
    @Accessors(chain = true)
    @NoArgsConstructor
    @ApiModel("Auth-RegistryFrom")
    public static class RegistryForm {
        @ApiModelProperty(value = "휴대폰번호", notes = "숫자아닌값은 서버에서 replace 처리함", example = "01027058989", required = true)
        @NotEmpty(message = "전화번호는 필수 입니다.")
        private String mobile;

        @ApiModelProperty(value = "패스워드", notes = "패스워드 제한 로직은 현재 없음", example = "your password", required = true)
        @NotEmpty(message = "패스워드는 필수 입니다.")
        private String password;

        @ApiModelProperty(value = "인증코드", notes = "4자리 숫자코드", example = "1357", required = true)
        @NotEmpty(message = "인증코드는 필수 입니다.")
        @Length(min=4, max=4, message = "4자리 인증코드를 넣어주세요.")
        private String otp;
    }


    @Data
    @Accessors(chain = true)
    @NoArgsConstructor
    @ApiModel("Auth-LoginForm")
    public static class LoginForm {
        @ApiModelProperty(value = "휴대폰번호", notes = "숫자아닌값은 서버에서 replace 처리함", example = "01027058989", required = true)
        @NotEmpty(message = "휴대폰번호는 필수 입니다.")
        private String mobile;

        @ApiModelProperty(value = "패스워드", notes = "패스워드 제한 로직은 현재 없음", example = "your password", required = true)
        @NotEmpty(message = "패스워드는 필수 입니다.")
        private String password;
    }

}
