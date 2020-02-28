package me.jigulsw.api.domain.member;

import me.jigulsw.api.domain.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
@Transactional(propagation = Propagation.REQUIRED)
public class MemberService extends BaseService<Member, Long> {
    @Autowired
    MemberRepository repository;
}
