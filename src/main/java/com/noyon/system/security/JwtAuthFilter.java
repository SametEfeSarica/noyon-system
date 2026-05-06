package com.noyon.system.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // 1. Header kontrolü (Token yoksa diğer filtrelere geç)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);
        final String email;

        try {
            // 2. Token geçerliliğini ve email'i güvenli şekilde (try-catch içinde) al
            if (!jwtService.isTokenValid(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            email = jwtService.extractUsername(token);

        } catch (Exception e) {
            // BÜYÜK DÜZELTME 1: Süresi dolmuş veya bozuk token gelirse sistem çökmesin,
            // sessizce işlemi iptal edip yetkisiz (403) saysın.
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Kullanıcıyı Authenticate (Giriş yapmış) olarak işaretleme
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);

            // BÜYÜK DÜZELTME 2: ClassCastException (Sistem çökme) Önlemi
            // Gelen UserDetails nesnesi GERÇEKTEN bizim Entity User sınıfımızsa dönüştür.
            if (userDetails instanceof com.noyon.system.entity.User) {
                com.noyon.system.entity.User user = (com.noyon.system.entity.User) userDetails;
                request.setAttribute("userId", user.getId());
            }
            // Eğer Spring'in varsayılan User sınıfıysa buraya girmez ve sistemi çökertmez!
        }

        filterChain.doFilter(request, response);
    }
}