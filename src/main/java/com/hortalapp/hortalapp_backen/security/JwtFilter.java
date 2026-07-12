package com.hortalapp.hortalapp_backen.security;


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
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        System.out.println(">>> JWT Filter - Header: " + header);
        System.out.println(">>> JWT Filter - URI: " + request.getRequestURI());

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                if (jwtUtil.validarToken(token)) {
                    String correo = jwtUtil.extraerCorreo(token);
                    System.out.println(">>> JWT válido para: " + correo);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(correo);
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } else {
                    System.out.println(">>> JWT inválido");
                }
            } catch (Exception e) {
                System.out.println(">>> Error en JWT: " + e.getMessage());
            }
        } else {
            System.out.println(">>> No hay token en el header");
        }
        chain.doFilter(request, response);
    }
}