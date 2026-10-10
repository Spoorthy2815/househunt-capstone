package com.househunt;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.Locale;
@RestController @RequestMapping("/api/auth")
class AuthController {
 final Users users;private final BCryptPasswordEncoder passwords;private final JwtEncoder encoder;
 AuthController(Users users,BCryptPasswordEncoder passwords,JwtEncoder encoder){this.users=users;this.passwords=passwords;this.encoder=encoder;}
 @PostMapping("/register") @ResponseStatus(HttpStatus.CREATED) AuthView register(@Valid @RequestBody RegisterRequest r){
 String email=r.email().trim().toLowerCase(Locale.ROOT);if(users.findByEmail(email).isPresent())throw new ResponseStatusException(HttpStatus.CONFLICT,"Email already registered");
 return token(users.save(new User(r.name().trim(),email,passwords.encode(r.password()),Role.USER)));
 }
 @PostMapping("/login") AuthView login(@Valid @RequestBody LoginRequest r){
 User u=users.findByEmail(r.email().trim().toLowerCase(Locale.ROOT)).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid email or password"));
 if(!passwords.matches(r.password(),u.password))throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid email or password");return token(u);
 }
 @GetMapping("/me") UserView me(@AuthenticationPrincipal Jwt jwt){return UserView.of(current(jwt));}
 User current(Jwt jwt){if(jwt==null)throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Sign in required");return users.findById(Long.valueOf(jwt.getSubject())).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Account unavailable"));}
 private AuthView token(User u){Instant now=Instant.now();var claims=JwtClaimsSet.builder().issuer("househunt").subject(u.id.toString()).issuedAt(now).expiresAt(now.plusSeconds(7200)).claim("role",u.role.name()).build();String token=encoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(),claims)).getTokenValue();return new AuthView(token,UserView.of(u));}
}