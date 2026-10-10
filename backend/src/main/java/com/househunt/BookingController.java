package com.househunt;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import jakarta.validation.Valid;
import java.util.List;
@RestController @RequestMapping("/api/bookings") @Transactional
class BookingController {
 final Bookings bookings; final Properties properties; final AuthController auth;
 BookingController(Bookings b,Properties p,AuthController a){bookings=b;properties=p;auth=a;}
 @GetMapping List<BookingView> list(@AuthenticationPrincipal Jwt jwt){return bookings.forUser(auth.current(jwt).id).stream().map(BookingView::of).toList();}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) BookingView create(@Valid @RequestBody BookingRequest r,@AuthenticationPrincipal Jwt jwt){
 User u=auth.current(jwt);Property p=properties.lock(r.propertyId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Property not found"));
 if(p.status!=PropertyStatus.APPROVED || p.owner.id.equals(u.id))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Property cannot be booked");
 if(!r.endDate().isAfter(r.startDate()))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"End date must be after start date");
 if(bookings.overlaps(p.id,-1L,r.startDate(),r.endDate())>0)throw new ResponseStatusException(HttpStatus.CONFLICT,"Property is already reserved for these dates");
 Booking b=new Booking();b.tenant=u;b.property=p;b.startDate=r.startDate();b.endDate=r.endDate();b.message=r.message();return BookingView.of(bookings.save(b));
 }
 @PatchMapping("/{id}") BookingView update(@PathVariable Long id,@Valid @RequestBody StatusRequest r,@AuthenticationPrincipal Jwt jwt){
 User u=auth.current(jwt);Booking b=bookings.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Booking not found"));
 properties.lock(b.property.id).orElseThrow();
 boolean owner=b.property.owner.id.equals(u.id)||u.role==Role.ADMIN;
 boolean tenant=b.tenant.id.equals(u.id);
 if(r.status().equals("CANCELLED") && tenant && (b.status==BookingStatus.REQUESTED || b.status==BookingStatus.ACCEPTED))b.status=BookingStatus.CANCELLED;
 else if(owner && b.status==BookingStatus.REQUESTED && List.of("ACCEPTED","DECLINED").contains(r.status())){
 if(r.status().equals("ACCEPTED") && bookings.overlaps(b.property.id,b.id,b.startDate,b.endDate)>0)throw new ResponseStatusException(HttpStatus.CONFLICT,"An accepted booking overlaps these dates");
 b.status=BookingStatus.valueOf(r.status());
 }else throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Booking transition is not allowed");
 return BookingView.of(b);
 }
}