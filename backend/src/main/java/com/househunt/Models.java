package com.househunt;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.Instant;
import java.math.BigDecimal;

enum Role { USER, ADMIN }
enum PropertyStatus { PENDING, APPROVED, REJECTED }
enum BookingStatus { REQUESTED, ACCEPTED, DECLINED, CANCELLED }

@Entity @Table(name="users")
class User {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(nullable=false) public String name;
 @Column(nullable=false,unique=true) public String email;
 @Column(nullable=false) public String password;
 @Enumerated(EnumType.STRING) @Column(nullable=false) public Role role=Role.USER;
 @OneToMany(mappedBy="owner") public java.util.List<Property> listings=new java.util.ArrayList<>();
 @ManyToMany @JoinTable(name="saved_properties",joinColumns=@JoinColumn(name="user_id"),inverseJoinColumns=@JoinColumn(name="property_id"))
 public java.util.Set<Property> savedProperties=new java.util.HashSet<>();
 @OneToOne(mappedBy="user",cascade=CascadeType.ALL) public UserProfile profile;
 protected User() {}
 User(String name,String email,String password,Role role) {this.name=name;this.email=email;this.password=password;this.role=role;}
}
@Entity @Table(name="properties",indexes={@Index(name="property_search",columnList="status,city,type,price")})
class Property {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 public String title;
 @Column(length=4000) public String description;
 public String city;
 public String address;
 public String type;
 public BigDecimal price;
 public int bedrooms;
 public int bathrooms;
 public int area;
 @Column(length=2000) public String amenities;
 @Column(length=2000) public String imageUrl;
 @Column(length=2000) public String tourUrl;
 @Enumerated(EnumType.STRING) public PropertyStatus status=PropertyStatus.PENDING;
 @ManyToOne(optional=false,fetch=FetchType.EAGER) public User owner;
 public Instant createdAt=Instant.now();
}
@Entity @Table(name="bookings",indexes=@Index(name="booking_dates",columnList="property_id,startDate,endDate"))
class Booking {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @ManyToOne(optional=false,fetch=FetchType.EAGER) public User tenant;
 @ManyToOne(optional=false,fetch=FetchType.EAGER) public Property property;
 public LocalDate startDate;
 public LocalDate endDate;
 @Column(length=1000) public String message;
 @Enumerated(EnumType.STRING) public BookingStatus status=BookingStatus.REQUESTED;
 public Instant createdAt=Instant.now();
}
@Entity @Table(name="chat_messages")
class ChatMessage {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @ManyToOne(optional=false,fetch=FetchType.EAGER) public User user;
 @Column(length=2000) public String question;
 @Column(length=8000) public String answer;
 public Instant createdAt=Instant.now();
}
@Entity @Table(name="user_profiles")
class UserProfile {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @OneToOne(optional=false) @JoinColumn(unique=true) public User user;
 public String phone;
 public String preferredCity;
}