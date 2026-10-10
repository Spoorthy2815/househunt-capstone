package com.househunt;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
class PropertyServiceTest {
 @Test void blocksDeletionByAnotherOwner(){
 Properties repository=mock(Properties.class);Bookings bookings=mock(Bookings.class);
 User owner=new User();owner.id=1L;User stranger=new User();stranger.id=2L;
 Property p=new Property();p.id=9L;p.owner=owner;when(repository.findById(9L)).thenReturn(Optional.of(p));
 assertThrows(ResponseStatusException.class,()->new PropertyService(repository,bookings).delete(9L,stranger));
 verify(repository,never()).delete(any());
 }
 @Test void vectorRetrievalRanksMatchingCityFirst(){
 Property a=new Property();a.city="Pune";a.title="Cozy studio";a.description="Light";a.type="Studio";a.amenities="Parking";
 Property b=new Property();b.city="Bengaluru";b.title="Large villa";b.description="Garden";b.type="Villa";b.amenities="Pool";
 assertSame(a,new ListingEmbeddings().retrieve("Pune studio",java.util.List.of(b,a)).getFirst());
 }
}