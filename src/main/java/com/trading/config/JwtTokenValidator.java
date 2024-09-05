package com.trading.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JwtTokenValidator extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = request.getHeader(JwtConstant.JWT_HEADER);
        if (jwt != null) {
            jwt = jwt.substring(7);
            try {
                SecretKey secretKey = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());
                Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(jwt).getBody();
                String email = String.valueOf(claims.get("email"));
                String authorities = claims.get("authorities").toString();
                List<GrantedAuthority> authorityList = AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);
                Authentication auth = new UsernamePasswordAuthenticationToken(email,null, authorityList);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception e) {
                throw new RuntimeException("Invalid JWT token", e);
            }
        }
        filterChain.doFilter(request, response);
    }
}
