package com.househunt;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.*;
import org.springframework.web.server.ResponseStatusException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.*;
record ProfileRequest(@Size(max=30) String phone,@Size(max=100) String preferredCity){}
@RestController @RequestMapping("/api/account") @Transactional
class ProfileController {
 final AuthController auth;final Properties properties;
 ProfileController(AuthController a,Properties p){auth=a;properties=p;}
 @GetMapping("/profile") Map<String,String> profile(@AuthenticationPrincipal Jwt jwt){User u=auth.current(jwt);return Map.of("phone",u.profile==null?"":Objects.toString(u.profile.phone,""),"preferredCity",u.profile==null?"":Objects.toString(u.profile.preferredCity,""));}
 @PutMapping("/profile") Map<String,String> update(@AuthenticationPrincipal Jwt jwt,@Valid @RequestBody ProfileRequest r){User u=auth.current(jwt);if(u.profile==null){u.profile=new UserProfile();u.profile.user=u;}u.profile.phone=r.phone();u.profile.preferredCity=r.preferredCity();auth.users.save(u);return profile(jwt);}
 @GetMapping("/favorites") List<PropertyView> saved(@AuthenticationPrincipal Jwt jwt){return auth.current(jwt).savedProperties.stream().filter(p->p.status==PropertyStatus.APPROVED).map(PropertyView::of).toList();}
 @PutMapping("/favorites/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) void save(@PathVariable Long id,@AuthenticationPrincipal Jwt jwt){User u=auth.current(jwt);Property p=properties.findById(id).filter(x->x.status==PropertyStatus.APPROVED).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Property not found"));u.savedProperties.add(p);}
 @DeleteMapping("/favorites/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) void remove(@PathVariable Long id,@AuthenticationPrincipal Jwt jwt){User u=auth.current(jwt);u.savedProperties.removeIf(p->p.id.equals(id));}
}