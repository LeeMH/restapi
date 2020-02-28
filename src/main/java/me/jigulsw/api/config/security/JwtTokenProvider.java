package me.jigulsw.api.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.Claim;
import me.jigulsw.api.common.Aes256;
import me.jigulsw.api.domain.member.MemberDto;
import me.jigulsw.api.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${biz.jwt.issuer}")
    private String issuer;

    @Value("${biz.secretkey}")
    private String jwtSecret;

    public String generateToken(Authentication authentication, int expiredMinutes) {
        UserDetailImpl userDetail = (UserDetailImpl)authentication.getPrincipal();
        return generateToken(userDetail.getId().toString(), userDetail.getMobile(), userDetail.getName(), userDetail.getEmail(), expiredMinutes);
    }

    public String generateToken(String id, String mobile, String name, String email, int expiredMinutes) {
        Date iat = new Date();
        Date exp = new Date(iat.getTime() + 60 * expiredMinutes * 1000);

        String claim;
        try {
            claim = String.format("%s,%s,%s,%s", id, mobile, name, email);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ApiException("UserDetail 정보 변환중 에러!!");
        }

        String encClaim = Aes256.encrypt(claim, jwtSecret);

        return JWT.create()
                .withIssuer(issuer)
                .withIssuedAt(iat)
                .withExpiresAt(exp)
                .withClaim("u", encClaim)
                .sign(Algorithm.HMAC512(jwtSecret));
    }

    public String extractInfoInJwt(String[] decClaim, int index, String key) {
        return Optional.ofNullable(decClaim)
                .map(it -> it[index])
                .orElseThrow(() -> new ApiException("토큰정보 분석중 에러 (" + key + ")"));
    }


    public MemberDto.BasicInfo getUserIdFromJWT(String token) {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC512(jwtSecret))
                .build();

        Claim jwt = verifier.verify(token).getClaim("u");
        String decClaim = Aes256.decrypt(jwt.asString(), jwtSecret);
        MemberDto.BasicInfo basicInfo = new MemberDto.BasicInfo();

        String[] tmp = decClaim.split(",");
        Long id = Long.valueOf(extractInfoInJwt(tmp, 0, "id"));
        String mobile = extractInfoInJwt(tmp, 1, "mobile");
        String name = extractInfoInJwt(tmp, 3, "name");
        String email = extractInfoInJwt(tmp, 4, "email");

        return basicInfo
                .setId(id)
                .setMobile(mobile)
                .setName(name)
                .setEmail(email);
    }

    public boolean validateTokenWithoutException(String authToken) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC512(jwtSecret))
                    .build();
            verifier.verify(authToken);

            return true;
        } catch(Exception e) {
            return false;
        }
    }

    public boolean validateToken(String authToken) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC512(jwtSecret))
                    .build();
            verifier.verify(authToken);

            return true;
        } catch(SignatureVerificationException e) {
            log.error("JWT token verification failed!!");
            log.error("input token = [" + authToken + "]");
            throw new ApiException("토근 검증 실패!!");
        }
    }

    public String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
