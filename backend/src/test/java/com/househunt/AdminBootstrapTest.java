package com.househunt;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class AdminBootstrapTest {
 @Test void doesNothingWithoutProvisioningSecrets(){
  Users users=mock(Users.class);
  new AdminBootstrap(users,new BCryptPasswordEncoder(),"","").run();
  verifyNoInteractions(users);
 }
 @Test void createsAdministratorWithHashedPassword(){
  Users users=mock(Users.class);when(users.findByEmail("operator@example.com")).thenReturn(Optional.empty());
  BCryptPasswordEncoder passwords=new BCryptPasswordEncoder();
  new AdminBootstrap(users,passwords,"operator@example.com","UniqueDeployPass123!").run();
  verify(users).save(argThat(u->u.role==Role.ADMIN && passwords.matches("UniqueDeployPass123!",u.password)));
 }
 @Test void neverOverwritesAnExistingAccount(){
  Users users=mock(Users.class);when(users.findByEmail("operator@example.com")).thenReturn(Optional.of(new User()));
  new AdminBootstrap(users,new BCryptPasswordEncoder(),"operator@example.com","UniqueDeployPass123!").run();
  verify(users,never()).save(any());
 }
}