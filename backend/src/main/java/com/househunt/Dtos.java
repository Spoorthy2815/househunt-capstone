package com.househunt;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.*;
record RegisterRequest(@NotBlank @Size(max=100) String name,@NotBlank @Email @Size(max=200) String email,@NotBlank @Size(min=8,max=72) String password) {}
record LoginRequest(@NotBlank @Email String email,@NotBlank String password) {}
record UserView(Long id,String name,String email,Role role) {static UserView of(User u){return new UserView(u.id,u.name,u.email,u.role);}}
record AuthView(String token,UserView user) {}
record PropertyRequest(@NotBlank @Size(max=150) String title,@NotBlank @Size(max=4000) String description,@NotBlank @Size(max=100) String city,@NotBlank @Size(max=250) String address,@NotNull @Pattern(regexp="Apartment|House|Studio|Villa") String type,@NotNull @DecimalMin("1") @DecimalMax("100000000") BigDecimal price,@Min(0) @Max(20) int bedrooms,@Min(1) @Max(20) int bathrooms,@Min(1) @Max(100000) int area,@Size(max=2000) String amenities,@Pattern(regexp="^$|https://[^\\s]+") @Size(max=2000) String imageUrl,@Pattern(regexp="^$|https://[^\\s]+") @Size(max=2000) String tourUrl) {}
record PropertyView(Long id,String title,String description,String city,String address,String type,BigDecimal price,int bedrooms,int bathrooms,int area,String amenities,String imageUrl,String tourUrl,PropertyStatus status,Long ownerId,String ownerName,Instant createdAt) {
 static PropertyView of(Property p){return new PropertyView(p.id,p.title,p.description,p.city,p.address,p.type,p.price,p.bedrooms,p.bathrooms,p.area,p.amenities,p.imageUrl,p.tourUrl,p.status,p.owner.id,p.owner.name,p.createdAt);}
}
record BookingRequest(@NotNull Long propertyId,@NotNull @FutureOrPresent LocalDate startDate,@NotNull @Future LocalDate endDate,@Size(max=1000) String message) {}
record BookingView(Long id,Long propertyId,String propertyTitle,Long ownerId,Long tenantId,String tenantName,LocalDate startDate,LocalDate endDate,String message,BookingStatus status) {
 static BookingView of(Booking b){return new BookingView(b.id,b.property.id,b.property.title,b.property.owner.id,b.tenant.id,b.tenant.name,b.startDate,b.endDate,b.message,b.status);}
}
record StatusRequest(@NotBlank String status) {}
record ChatRequest(@NotBlank @Size(max=1000) String message) {}
record ChatView(Long id,String question,String answer,Instant createdAt) { static ChatView of(ChatMessage m){return new ChatView(m.id,m.question,m.answer,m.createdAt);} }