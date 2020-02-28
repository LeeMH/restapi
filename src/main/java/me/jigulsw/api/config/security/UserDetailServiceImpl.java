package me.jigulsw.api.config.security;

import me.jigulsw.api.domain.member.Member;
import me.jigulsw.api.domain.member.MemberRepository;
import me.jigulsw.api.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Slf4j
@Service
public class UserDetailServiceImpl implements UserDetailsService {
    @Autowired
    MemberRepository repository;

    @Override
    public UserDetails loadUserByUsername(String mobile) {
        Optional<Member> member = repository.findByMobile(mobile);

        return processLogin(member);
    }

    public UserDetailImpl loadUserById(Long id) {
        Optional<Member> member = repository.findById(id);

        return processLogin(member);
    }

    public UserDetailImpl processLogin(Optional<Member> member) throws UsernameNotFoundException {
        if (!member.isPresent()) {
            throw new ApiException("회원정보를 찾지 못했습니다. 입력하신 정보를 확인해 주세요.");
        }

        if (member.get().isActive()) {
            throw new ApiException("탈퇴한 회원 입니다.\n 재가입을 원하시는 경우 문의하기를 통해 요청해주세요.");
        }

        return UserDetailImpl.create(member.get());
    }
}
