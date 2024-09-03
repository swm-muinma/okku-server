package kr.okku.server.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.okku.server.enums.RoleEnum;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        System.out.println("call");
        String token = jwtTokenProvider.resolveToken(request);

        if (token != null && jwtTokenProvider.validateAccessToken(token)) {
            String userId = jwtTokenProvider.getUserIdFromAccessToken(token);
            List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(RoleEnum.USER.getValue()));

            UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                    .username(userId)
                    .password("")
                    .authorities(authorities)
                    .build();

            PreAuthenticatedAuthenticationToken auth = new PreAuthenticatedAuthenticationToken(userDetails, null, authorities);
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);
            System.out.println( SecurityContextHolder.getContext().getAuthentication());
            System.out.println("Authenticated user: " + userId);
            System.out.println("Authorities: " + authorities);
        }

        filterChain.doFilter(request, response);

    }
}
