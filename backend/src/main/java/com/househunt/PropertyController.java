package com.househunt;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
@RestController @RequestMapping("/api") @Transactional(readOnly=true)
class PropertyController {
 final PropertyService service;final AuthController auth;
 PropertyController(PropertyService s,AuthController a){service=s;auth=a;}
 @GetMapping("/properties") Page<PropertyView> search(@RequestParam(defaultValue="") String city,@RequestParam(defaultValue="") String type,@RequestParam(defaultValue="0") BigDecimal min,@RequestParam(defaultValue="100000000") BigDecimal max,@RequestParam(defaultValue="") String q,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="createdAt") String sort){return service.search(city,type,min,max,q,page,sort);}
 @GetMapping("/properties/{id}") PropertyView detail(@PathVariable Long id,@AuthenticationPrincipal Jwt jwt){return service.detail(id,jwt==null?null:auth.current(jwt));}
 @GetMapping("/dashboard/properties") List<PropertyView> owned(@AuthenticationPrincipal Jwt jwt){return service.owned(auth.current(jwt).id);}
 @PostMapping("/properties") @ResponseStatus(HttpStatus.CREATED) @Transactional PropertyView create(@Valid @RequestBody PropertyRequest r,@AuthenticationPrincipal Jwt jwt){return service.save(null,r,auth.current(jwt));}
 @PutMapping("/properties/{id}") @Transactional PropertyView edit(@PathVariable Long id,@Valid @RequestBody PropertyRequest r,@AuthenticationPrincipal Jwt jwt){return service.save(id,r,auth.current(jwt));}
 @DeleteMapping("/properties/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) @Transactional void delete(@PathVariable Long id,@AuthenticationPrincipal Jwt jwt){service.delete(id,auth.current(jwt));}
 @GetMapping("/admin/properties") @PreAuthorize("hasRole('ADMIN')") Page<PropertyView> queue(@RequestParam(defaultValue="PENDING") PropertyStatus status,@RequestParam(defaultValue="0") int page){return service.moderationQueue(status,page);}
 @PatchMapping("/admin/properties/{id}") @PreAuthorize("hasRole('ADMIN')") @Transactional PropertyView moderate(@PathVariable Long id,@Valid @RequestBody StatusRequest r){return service.moderate(id,r.status());}
 @GetMapping("/admin/users") @PreAuthorize("hasRole('ADMIN')") Page<UserView> users(@RequestParam(defaultValue="0") int page){return auth.users.findAll(PageRequest.of(Math.max(page,0),20)).map(UserView::of);}
}