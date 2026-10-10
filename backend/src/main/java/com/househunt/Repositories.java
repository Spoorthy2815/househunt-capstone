package com.househunt;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.*;
import jakarta.persistence.LockModeType;
import java.util.*;
import java.time.LocalDate;
import java.math.BigDecimal;
interface Users extends JpaRepository<User,Long> { Optional<User> findByEmail(String email); }
interface Properties extends JpaRepository<Property,Long> {
 @Query(value="select p from Property p join fetch p.owner where p.status = :status and (:city = '' or lower(p.city) like lower(concat('%',:city,'%'))) and (:type = '' or p.type = :type) and p.price between :min and :max and (:q = '' or lower(p.title) like lower(concat('%',:q,'%')) or lower(p.description) like lower(concat('%',:q,'%')))",
 countQuery="select count(p) from Property p where p.status = :status and (:city = '' or lower(p.city) like lower(concat('%',:city,'%'))) and (:type = '' or p.type = :type) and p.price between :min and :max and (:q = '' or lower(p.title) like lower(concat('%',:q,'%')) or lower(p.description) like lower(concat('%',:q,'%')))")
 Page<Property> search(@Param("status") PropertyStatus status,@Param("city") String city,@Param("type") String type,@Param("min") BigDecimal min,@Param("max") BigDecimal max,@Param("q") String q,Pageable pageable);
 @Query("select p from Property p join fetch p.owner where p.owner.id=:id order by p.createdAt desc") List<Property> owned(@Param("id") Long id);
 @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select p from Property p where p.id=:id") Optional<Property> lock(@Param("id") Long id);
}
interface Bookings extends JpaRepository<Booking,Long> {
 @Query("select b from Booking b join fetch b.property p join fetch p.owner join fetch b.tenant where b.tenant.id=:id or p.owner.id=:id order by b.createdAt desc") List<Booking> forUser(@Param("id") Long id);
 @Query("select count(b) from Booking b where b.property.id=:property and b.status=com.househunt.BookingStatus.ACCEPTED and b.id<>:exclude and b.startDate<:end and b.endDate>:start")
 long overlaps(@Param("property") Long property,@Param("exclude") Long exclude,@Param("start") LocalDate start,@Param("end") LocalDate end);
 boolean existsByPropertyId(Long id);
}
interface Chats extends JpaRepository<ChatMessage,Long> { List<ChatMessage> findTop20ByUserIdOrderByCreatedAtDesc(Long id); }