package me.jigulsw.api.domain.otp;

import me.jigulsw.api.common.DateUtil;
import me.jigulsw.api.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@Slf4j
public class OtpRepositoryTest {

    @Autowired
    OtpRepository otpRepository;

    @Test
    public void 인증번호_추출_테스트() {
        //when : 인증번호 10개를 저장했을때,
        int size = 10;
        인증번호_저장(1, size);

        //then : 10개가 저장되어 있어야 한다.
        List<Otp> otps = otpRepository.findAll();
        assertThat(otps.size()).isEqualTo(size);

        //when : 최신순으로 정렬을 했다면,,
        List<Long> ids = otps.stream().map(it -> it.getId()).collect(Collectors.toList());
        Collections.sort(ids);
        otps.stream().forEach(
                it -> log.info("[{}], [{}]", it.getId(), it.getCreatedAt())
        );

        //then : 정렬된 마지막 데이터와 find로 찾은 값이 같아야 한다.
        Otp actual = otpRepository.findFirstByMemberIdOrderByIdDesc(1)
                .orElseThrow(() -> new ApiException("OTP를 찾지 못했습니다."));
        Otp expected = otpRepository.findById(ids.get(ids.size()-1))
                .orElseThrow(() -> new ApiException("OTP를 찾지 못했습니다."));

        log.info("선택된 otp id = [{}]", actual.getId());
        assertThat(actual.getId()).isEqualTo(expected.getId());
    }

    public void 인증번호_저장(long memberId, int count) {
        for(int ii = 0; ii < count; ii++) {
            String authNo = StringUtils.right(String.valueOf(DateUtil.getNowMilli()), 4);
            Otp opt = new Otp().setAuthNo(authNo)
                    .setMemberId(memberId)
                    .setCreatedAt(DateUtil.getNow());

            otpRepository.save(opt);
        }
    }

}