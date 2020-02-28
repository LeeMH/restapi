package me.jigulsw.api.domain.otp;

import me.jigulsw.api.common.DateUtil;
import me.jigulsw.api.domain.BaseService;
import me.jigulsw.api.domain.member.Member;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional(propagation = Propagation.REQUIRED)
@Profile({"prod"})
public class OtpService extends BaseService<Otp, Long> implements IOtpService {

    @Autowired
    OtpRepository otpRepository;

    @Override
    public Otp generateOtp(Member member) {
        String authNo = StringUtils.right(String.valueOf(DateUtil.getNowMilli()), 4);
        Otp otp = new Otp().setMemberId(member.getId())
                .setAuthNo(authNo)
                .setCreatedAt(DateUtil.getNow());

        return otpRepository.save(otp);
    }

    @Override
    public void generateOtpAndSendIt(Member member) {
        Otp otp = generateOtp(member);

        //TODO : SMS 전송하는 로직이 필요함.
        log.info("전송됨 ==> Mobile[{}], AuthNo[{}]", member.getMobile(), otp.getAuthNo());
    }
}
