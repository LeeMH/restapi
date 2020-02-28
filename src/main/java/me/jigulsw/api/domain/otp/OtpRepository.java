package me.jigulsw.api.domain.otp;

import me.jigulsw.api.domain.BaseRepository;

import java.util.Optional;

public interface OtpRepository extends BaseRepository<Otp, Long> {
    Optional<Otp> findFirstByMemberIdOrderByIdDesc(long memberId);
}
