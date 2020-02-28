package me.jigulsw.api.controller;

import me.jigulsw.api.config.security.JwtAuthenticationResponse;
import me.jigulsw.api.config.security.JwtTokenProvider;
import me.jigulsw.api.domain.auth.AuthDto;
import me.jigulsw.api.domain.member.Member;
import me.jigulsw.api.domain.member.MemberRepository;
import me.jigulsw.api.domain.otp.IOtpService;
import me.jigulsw.api.exception.ApiException;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/auth")
@io.swagger.annotations.Api(value = "AuthController 로그인/가입 관련")
public class AuthController extends BaseController{
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider tokenProvider;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    IOtpService otpService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${biz.jwt.expired}")
    int jwtExpiredMinutes;

    @Value("${biz.jwt.refresh.expired}")
    int refreshJwtExpiredMinutes;

    @ApiOperation(value = "인증번호 요청", notes = "회원가입시 휴대폰번호 검증을 위해 사용")
    @PostMapping(value = "/otp", produces = APPLICATION_JSON)
    public ApiResponse sendOtp(@Valid @RequestBody AuthDto.MobileForm form) {
        log.info("REQ :: {}", form.toString());
        Optional<Member> member = memberRepository.findByMobile(form.getMobile());
        if (member.isPresent()) {
            throw new ApiException("이미 등록된 회원 입니다.");
        }

        Member newMember = new Member()
                .setMobile(form.getMobile());
        newMember = memberRepository.save(newMember);

        //OTP 전송
        otpService.generateOtpAndSendIt(newMember);

        log.info("RES :: OK");
        return ApiResponse.ok();
    }

    @ApiOperation(value = "회원가입", notes = "인증문자와 가입정보를 넣어 회원가입")
    @PostMapping(value = "/register", produces = APPLICATION_JSON)
    public ApiResponse register(@Valid @RequestBody AuthDto.RegistryForm form) {
        log.info("REQ :: {}", form.toString());
        Member member = memberRepository.findByMobile(form.getMobile())
                .orElseThrow(() -> new ApiException("고객정보를 찾을수 없습니다."));

        //TODO : SMS는 무조건 1111이라고 가정
        if (!form.getOtp().equals("1111")) {
            throw new ApiException("인증번호가 옳바르지 않습니다.");
        }

        member.setPassword(passwordEncoder.encode(form.getPassword()));
        memberRepository.save(member);
        log.info("RES :: OK");

        return ApiResponse.ok();
    }

    @ApiOperation(value = "로그인", notes = "휴대폰번호와 비밀번호로 로그인, 성공시 jwt리턴")
    @PostMapping(value = "/login", produces = APPLICATION_JSON)
    public ApiResponse<JwtAuthenticationResponse> login(@Valid @RequestBody AuthDto.LoginForm form) {
        log.info("REQ :: {}", form.toString());
        Member member = memberRepository.findByMobile(form.getMobile())
                .orElseThrow(() -> new ApiException("입력하신 휴대폰의 회원을 찾지 못했습니다."));


        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        form.getMobile(), form.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.info("RES :: OK");

        return ApiResponse.ok(makeAuthResponse(member));
    }

    @ApiOperation(value = "권한테스트", notes = "권한이 없으면 안되요.")
    @PostMapping(value = "/role_test", produces = APPLICATION_JSON)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ApiResponse test() {
        return ApiResponse.ok();
    }

    private JwtAuthenticationResponse makeAuthResponse(Member member) {
        String token = tokenProvider.generateToken(
                String.valueOf(member.getId()), member.getMobile(), member.getName(), member.getEmail(),
                jwtExpiredMinutes);

        String refreshToken = tokenProvider.generateToken(
                String.valueOf(member.getId()), member.getMobile(), member.getName(), member.getEmail(),
                refreshJwtExpiredMinutes);

        return new JwtAuthenticationResponse()
                .setAccessToken(token)
                .setRefreshToken(refreshToken)
                .setId(member.getId())
                .setName(member.getDisplayName());
    }


}
