package me.jigulsw.api.config.security;

import me.jigulsw.api.domain.member.MemberDto;
import me.jigulsw.api.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserDetailServiceImpl userDetailService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = tokenProvider.getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                MemberDto.BasicInfo basicInfo = tokenProvider.getUserIdFromJWT(jwt);

                UserDetailImpl userDetails = userDetailService.loadUserById(basicInfo.getId());

                if (Objects.isNull(userDetails) ||
                        !userDetails.getId().equals(basicInfo.getId()) ||
                        !userDetails.getMobile().equals(basicInfo.getMobile())) {
                    log.error("고객정보와 토큰정보가 일치 하지 않습니다.");
                    log.error("db [{}], token [{}]", userDetails.toString(), basicInfo.toString());
                    throw new ApiException("고객정보와 요청된 토큰의 고객정보가 일치 하지 않습니다.");
                }

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                request.setAttribute("userId", basicInfo.getId());
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }


}