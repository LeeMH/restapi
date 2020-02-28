package me.jigulsw.api.domain.otp;

import me.jigulsw.api.domain.member.Member;

public interface IOtpService {
    Otp generateOtp(Member member);
    void generateOtpAndSendIt(Member member);
    //void verify(Member member, String userAuthNo);
}
