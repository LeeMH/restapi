package me.jigulsw.api.domain.member;

import me.jigulsw.api.domain.BaseRepository;

import java.util.Optional;

public interface MemberRepository extends BaseRepository<Member, Long> {
    Optional<Member> findByMobile(String mobile);
}
