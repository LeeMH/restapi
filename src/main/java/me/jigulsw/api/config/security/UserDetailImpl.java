package me.jigulsw.api.config.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import me.jigulsw.api.domain.member.Member;
import me.jigulsw.api.exception.ApiException;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailImpl implements UserDetails {

    private Long id;

    @JsonIgnore
    private String mobile;

    @JsonIgnore
    private String password;

    private String name;

    private String email;

    private Collection<? extends GrantedAuthority> authorities;

    public static UserDetailImpl create(Member member) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add( new SimpleGrantedAuthority("ROLE_USER"));

        if (Objects.isNull(member)) throw new ApiException("로그인 실패 입니다.");

        // 바디프랜드 메일 소유자라면 어드민 권한 부여
        if (Objects.nonNull(member.getEmail()) && member.getEmail().endsWith("@bodyfriend.co.kr")) {
            authorities.add( new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        return new UserDetailImpl(
                member.getId(),
                member.getMobile(),
                member.getPassword(),
                StringUtils.isEmpty(member.getName())? "고객님" : member.getName(),
                member.getEmail(),
                authorities
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDetailImpl that = (UserDetailImpl) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
