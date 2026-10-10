package com.househunt;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.util.*;
@Service @Transactional
class PropertyService {
 private final Properties properties; private final Bookings bookings;
 PropertyService(Properties p,Bookings b){properties=p;bookings=b;}
 @Transactional(readOnly=true) @Cacheable("search")
 public Page<PropertyView> search(String city,String type,BigDecimal min,BigDecimal max,String q,int page,String sort){
 if(min.signum()<0 || max.compareTo(min)<0 || page<0 || !List.of("createdAt","price").contains(sort))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid search filters");
 return properties.search(PropertyStatus.APPROVED,city,type,min,max,q,PageRequest.of(page,9,Sort.by(sort.equals("price")?Sort.Direction.ASC:Sort.Direction.DESC,sort))).map(PropertyView::of);
 }
 @Transactional(readOnly=true)
 public List<PropertyView> owned(Long userId) {
  return properties.owned(userId).stream().map(PropertyView::of).toList();
 }
 @Transactional(readOnly=true)
 public Page<PropertyView> moderationQueue(PropertyStatus status,int page) {
  return properties.search(status,"","",BigDecimal.ZERO,new BigDecimal("100000000"),"",
   PageRequest.of(Math.max(page,0),20,Sort.by("createdAt").descending())).map(PropertyView::of);
 }
 @Transactional(readOnly=true) public PropertyView detail(Long id,User u){Property p=get(id);if(p.status!=PropertyStatus.APPROVED && (u==null || (u.role!=Role.ADMIN && !p.owner.id.equals(u.id))))throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Property not found");return PropertyView.of(p);}
 @CacheEvict(value="search",allEntries=true) public PropertyView save(Long id,PropertyRequest r,User u){Property p=id==null?new Property():get(id);if(id!=null)authorize(p,u);p.owner=id==null?u:p.owner;
 p.title=r.title();p.description=r.description();p.city=r.city();p.address=r.address();p.type=r.type();p.price=r.price();p.bedrooms=r.bedrooms();p.bathrooms=r.bathrooms();p.area=r.area();p.amenities=r.amenities();p.imageUrl=r.imageUrl();p.tourUrl=r.tourUrl();p.status=PropertyStatus.PENDING;return PropertyView.of(properties.save(p));}
 @CacheEvict(value="search",allEntries=true) public void delete(Long id,User u){Property p=get(id);authorize(p,u);if(bookings.existsByPropertyId(id))throw new ResponseStatusException(HttpStatus.CONFLICT,"A listing with booking history cannot be deleted");properties.delete(p);}
 @CacheEvict(value="search",allEntries=true) public PropertyView moderate(Long id,String status){Property p=get(id);if(!List.of("APPROVED","REJECTED").contains(status))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Choose APPROVED or REJECTED");p.status=PropertyStatus.valueOf(status);return PropertyView.of(p);}
 Property get(Long id){return properties.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Property not found"));}
 void authorize(Property p,User u){if(u.role!=Role.ADMIN && !p.owner.id.equals(u.id))throw new ResponseStatusException(HttpStatus.FORBIDDEN,"You cannot manage this listing");}
}