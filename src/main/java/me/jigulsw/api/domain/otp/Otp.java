package me.jigulsw.api.domain.otp;

import me.jigulsw.api.common.DateUtil;
import me.jigulsw.api.exception.ApiException;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Slf4j
@Getter@Setter
@Accessors(chain = true)
@DynamicInsert
@DynamicUpdate
@Entity
public class Otp {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private long memberId;

    @Column
    private String authNo;

    @Column
    private long createdAt;

    public void verify(String userAuthNo) {
        if (!authNo.equals(userAuthNo)) {
            throw new ApiException("인증번호가 일치하지 않습니다.");
        }

        if (DateUtil.getNow() - createdAt > 180) {
            throw new ApiException("인증유효시간 3분이 초과되었습니다.");
        }
    }
}