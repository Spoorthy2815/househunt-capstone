package com.househunt;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.*;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.nio.charset.StandardCharsets;
import java.util.List;
@Configuration @EnableMethodSecurity
class SecurityConfig {
 @Bean BCryptPasswordEncoder passwords(){return new BCryptPasswordEncoder();}
 @Bean SecretKeySpec key(@Value("${app.jwt-secret}") String secret){if(secret.length()<32)throw new IllegalStateException("JWT_SECRET needs at least 32 characters");return new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),"HmacSHA256");}
 @Bean JwtEncoder encoder(SecretKeySpec key){return new NimbusJwtEncoder(new ImmutableSecret<>(key));}
 @Bean JwtDecoder decoder(SecretKeySpec key){var d=NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();d.setJwtValidator(JwtValidators.createDefaultWithIssuer("househunt"));return d;}
 @Bean SecurityFilterChain filter(HttpSecurity http,@Value("${app.cors-origin}") String origin)throws Exception {
 var cors=new CorsConfiguration();cors.setAllowedOrigins(List.of(origin));cors.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));cors.setAllowedHeaders(List.of("Authorization","Content-Type"));
 var source=new UrlBasedCorsConfigurationSource();source.registerCorsConfiguration("/**",cors);
 var authorities=new JwtGrantedAuthoritiesConverter();authorities.setAuthoritiesClaimName("role");authorities.setAuthorityPrefix("ROLE_");
 var converter=new JwtAuthenticationConverter();converter.setJwtGrantedAuthoritiesConverter(authorities);
 return http.csrf(c->c.disable()).cors(c->c.configurationSource(source)).sessionManagement(c->c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
 .authorizeHttpRequests(a->a.dispatcherTypeMatchers(jakarta.servlet.DispatcherType.FORWARD,jakarta.servlet.DispatcherType.ERROR).permitAll()
 .requestMatchers(org.springframework.http.HttpMethod.GET,"/","/index.html","/assets/**","/placeholder.svg","/login","/register","/dashboard","/account","/list","/edit/*","/property/*").permitAll()
 .requestMatchers("/api/auth/**","/actuator/health","/swagger-ui/**","/v3/api-docs/**").permitAll().requestMatchers(org.springframework.http.HttpMethod.GET,"/api/properties","/api/properties/*").permitAll().anyRequest().authenticated())
 .oauth2ResourceServer(o->o.jwt(j->j.jwtAuthenticationConverter(converter))).build();
 }
}