package me.jigulsw.api.controller;

import com.google.gson.Gson;
import me.jigulsw.api.BaseMockTest;
import me.jigulsw.api.domain.auth.AuthDto;
import me.jigulsw.api.domain.member.Member;
import me.jigulsw.api.domain.member.MemberRepository;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class AuthControllerTest extends BaseMockTest {

    @Autowired
    private AuthController authController;

    @Autowired
    private MemberRepository memberRepository;

    private Gson gson = new Gson();

    @Override
    protected Object controller() {
        return authController;
    }

    public String getNewMobileNumber() {
        return "010" + StringUtils.right(String.valueOf(Instant.now().toEpochMilli()) , 8);
    }

    public AuthDto.RegistryForm getNewMemberForm() {
        return new AuthDto.RegistryForm()
                .setMobile(getNewMobileNumber())
                .setPassword("aaa111")
                .setOtp("1111");
    }



    @Test
    public void SMS요청하기() throws Exception {
        Gson gson = new Gson();
        AuthDto.MobileForm form = new AuthDto.MobileForm()
                .setMobile(getNewMobileNumber());

        MockHttpServletRequestBuilder message = post("/api/v1/auth/otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(form));


        mockMvc.perform(message)
                .andExpect(status().isOk());
    }

    @Test
    public void 미등록_전화번호_가입() throws Exception {
        //when : 등록되지 않은 고객이 등록을 요청한다.
        AuthDto.RegistryForm form = getNewMemberForm();
        MockHttpServletRequestBuilder message = post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(form));

        //then : 에러가 발생하고, "고객정보를 찾을수 없습니다."라는 메세지를 수신한다.
        mockMvc.perform(message)
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message", containsString("찾을수 없습니다.")));
    }

    @Test
    public void 등록_전화번호_가입() throws Exception {
        AuthDto.RegistryForm form = getNewMemberForm();
        전화번호_가입(form);
    }

    public void 전화번호_가입(AuthDto.RegistryForm form) throws Exception {
        //when : 등록된 고객이 등록을 요청한다.
        Member member = new Member().setMobile(form.getMobile());
        memberRepository.save(member);

        MockHttpServletRequestBuilder message = post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(form));

        //then : 패스워드 항목은 null이다.
        assertThat(member.getPassword()).isNull();

        //then : 정상적으로 처리된다.
        mockMvc.perform(message)
                .andExpect(status().isOk());

        //then : 패스워드가 설정되어 있다.
        Member selectedMember = memberRepository.findByMobile(member.getMobile())
                .orElseThrow(() -> new RuntimeException("사용자가 없으면 테스트 실패"));
        assertThat(selectedMember.getPassword()).isNotNull();
    }


    @Test
    public void 로그인실패_패스워드가_틀린경우() throws Exception {
        AuthDto.RegistryForm form = getNewMemberForm();
        전화번호_가입(form);

        //when : 패스워드가 틀린경우
        form.setPassword("틀린비밀번호");
        MockHttpServletRequestBuilder message = post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(form));

        //then : 처리실패와 "Bad credentials"라는 메세지가 리턴된다.
        mockMvc.perform(message)
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message", is("권한이 없거나, 인증에 실패 했습니다.")));
    }

    @Test
    public void 로그인성공() throws Exception {
        AuthDto.RegistryForm form = getNewMemberForm();
        전화번호_가입(form);

        //when : 로그인정보가 맞다면,
        MockHttpServletRequestBuilder message = post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(form));

        //then : 성공적으로 로그인되고, token이 생성된다.
        mockMvc.perform(message)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken", not(emptyString())))
                .andExpect(jsonPath("$.data.refreshToken", not(emptyString())))
                .andExpect(jsonPath("$.data.name", not(emptyString()))); //이름은 실명인증이후 등록된다.
    }

    @Test
    public void 롤테스트() throws Exception {
        MockHttpServletRequestBuilder message = post("/api/v1/auth/role_test")
                .contentType(MediaType.APPLICATION_JSON);


        //then : 성공적으로 로그인되고, token이 생성된다.
        mockMvc.perform(message)
                .andExpect(status().is4xxClientError());
    }

}
