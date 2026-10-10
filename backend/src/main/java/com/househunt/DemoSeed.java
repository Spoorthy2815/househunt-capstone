package com.househunt;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
@Component
class DemoSeed implements CommandLineRunner {
 final Users users;final Properties properties;final BCryptPasswordEncoder passwords;final boolean enabled;
 DemoSeed(Users u,Properties p,BCryptPasswordEncoder b,@Value("${app.seed}") boolean enabled){users=u;properties=p;passwords=b;this.enabled=enabled;}
 @Transactional public void run(String...args){if(!enabled || users.count()>0)return;
 users.save(new User("HouseHunt Admin","admin@househunt.local",passwords.encode("AdminDemo123!"),Role.ADMIN));
 User owner=users.save(new User("Priya Sharma","owner@househunt.local",passwords.encode("OwnerDemo123!"),Role.USER));
 users.save(new User("Alex Morgan","renter@househunt.local",passwords.encode("RenterDemo123!"),Role.USER));
 String[] names={"The Palm Residence","Skyline Studio","Garden View Villa","Urban Nest","Lakefront Retreat","The Terracotta House"};
 String[] cities={"Bengaluru","Hyderabad","Pune","Bengaluru","Mumbai","Chennai"};
 String[] types={"Apartment","Studio","Villa","Apartment","Apartment","House"};
 String[] photos={"photo-1600585154340-be6161a56a0c","photo-1600210492486-724fe5c67fb0","photo-1613977257363-707ba9348227","photo-1600607687920-4e2a09cf159d","photo-1600566753086-00f18fb6b3ea","photo-1600047509807-ba8f99d2cdde"};
 int[] prices={28000,16000,48000,22000,65000,32000};
 for(int i=0;i<names.length;i++){Property p=new Property();p.title=names[i];p.city=cities[i];p.address=(12+i)+" Park Avenue, "+cities[i];p.description="A thoughtfully designed home with generous natural light, welcoming living spaces, and convenient access to the city. Schedule your stay with the owner through HouseHunt.";p.type=types[i];p.price=BigDecimal.valueOf(prices[i]);p.bedrooms=i==1?0:2+i%2;p.bathrooms=i==1?1:2;p.area=650+i*230;p.amenities="Parking, Wi-Fi, Balcony, Security";p.imageUrl="https://images.unsplash.com/"+photos[i]+"?auto=format&fit=crop&w=1000&q=80";p.tourUrl="";p.owner=owner;p.status=i==5?PropertyStatus.PENDING:PropertyStatus.APPROVED;properties.save(p);}
 }
}