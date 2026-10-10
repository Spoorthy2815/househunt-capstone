package com.househunt;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import java.util.Locale;
/** Create a deployment administrator only from operator-provided environment secrets. */
@Component
class AdminBootstrap implements CommandLineRunner {
 final Users users;final BCryptPasswordEncoder passwords;final String email,password;
 AdminBootstrap(Users users,BCryptPasswordEncoder passwords,
  @Value("${app.bootstrap-admin-email}") String email,
  @Value("${app.bootstrap-admin-password}") String password){
  this.users=users;this.passwords=passwords;this.email=email.trim().toLowerCase(Locale.ROOT);this.password=password;
 }
 @Transactional public void run(String...args){
  if(email.isBlank() && password.isBlank())return;
  if(!email.contains("@") || password.length()<12 || password.length()>72)
   throw new IllegalStateException("Provide a valid BOOTSTRAP_ADMIN_EMAIL and a 12-72 character BOOTSTRAP_ADMIN_PASSWORD");
  if(users.findByEmail(email).isEmpty())
   users.save(new User("HouseHunt Admin",email,passwords.encode(password),Role.ADMIN));
 }
}