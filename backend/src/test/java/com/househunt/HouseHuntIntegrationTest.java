package com.househunt;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.*;
import java.time.LocalDate;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(properties={"spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1","spring.jpa.hibernate.ddl-auto=create-drop","app.seed=true","app.llm-key="})
@AutoConfigureMockMvc
class HouseHuntIntegrationTest {
 @Autowired MockMvc mvc; @Autowired ObjectMapper json;
 String login(String email,String password)throws Exception{
 var result=mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(java.util.Map.of("email",email,"password",password)))).andExpect(status().isOk()).andReturn();
 return "Bearer "+json.readTree(result.getResponse().getContentAsString()).get("token").asText();
 }
 @Test void completeRentalLifecycleAndAccessControl()throws Exception{
 String owner=login("owner@househunt.local","OwnerDemo123!"),admin=login("admin@househunt.local","AdminDemo123!"),renter=login("renter@househunt.local","RenterDemo123!");
 String body="""
 {"title":"Integration home","description":"Bright home with Wi-Fi","city":"TestCity","address":"10 Test Road","type":"Apartment","price":23000,"bedrooms":2,"bathrooms":1,"area":900,"amenities":"Wi-Fi","imageUrl":"","tourUrl":""}
 """;
 var result=mvc.perform(post("/api/properties").header("Authorization",owner).contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isCreated()).andExpect(jsonPath("$.status").value("PENDING")).andReturn();
 long id=json.readTree(result.getResponse().getContentAsString()).get("id").asLong();
 mvc.perform(get("/api/properties/"+id)).andExpect(status().isNotFound());
 mvc.perform(put("/api/properties/"+id).header("Authorization",renter).contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isForbidden());
 mvc.perform(patch("/api/admin/properties/"+id).header("Authorization",renter).contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"APPROVED\"}")).andExpect(status().isForbidden());
 mvc.perform(patch("/api/admin/properties/"+id).header("Authorization",admin).contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"APPROVED\"}")).andExpect(status().isOk());
 mvc.perform(get("/api/properties").param("city","TestCity").param("max","24000")).andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(1));
 mvc.perform(get("/api/properties").param("city","TestCity").param("max","22000")).andExpect(jsonPath("$.totalElements").value(0));
 var request=java.util.Map.of("propertyId",id,"startDate",LocalDate.now().plusDays(5).toString(),"endDate",LocalDate.now().plusDays(20).toString(),"message","Please confirm");
 var booking=mvc.perform(post("/api/bookings").header("Authorization",renter).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(request))).andExpect(status().isCreated()).andReturn();
 long bookingId=json.readTree(booking.getResponse().getContentAsString()).get("id").asLong();
 mvc.perform(patch("/api/bookings/"+bookingId).header("Authorization",renter).contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"ACCEPTED\"}")).andExpect(status().isBadRequest());
 mvc.perform(patch("/api/bookings/"+bookingId).header("Authorization",owner).contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"ACCEPTED\"}")).andExpect(status().isOk());
 mvc.perform(post("/api/bookings").header("Authorization",renter).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(request))).andExpect(status().isConflict());
 mvc.perform(delete("/api/properties/"+id).header("Authorization",owner)).andExpect(status().isConflict());
 mvc.perform(patch("/api/bookings/"+bookingId).header("Authorization",renter).contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"CANCELLED\"}")).andExpect(status().isOk());
 mvc.perform(post("/api/chat").header("Authorization",renter).contentType(MediaType.APPLICATION_JSON).content("{\"message\":\"Find a home in TestCity\"}")).andExpect(status().isOk()).andExpect(jsonPath("$.answer").isNotEmpty());
 mvc.perform(get("/api/chat").header("Authorization",renter)).andExpect(status().isOk()).andExpect(jsonPath("$[0].question").value("Find a home in TestCity"));
 }
 @Test void registrationValidationAndAnonymousSecurity()throws Exception{
 mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"New User\",\"email\":\"new@test.local\",\"password\":\"SecurePass123!\"}")).andExpect(status().isCreated()).andExpect(jsonPath("$.user.role").value("USER")).andExpect(jsonPath("$.user.password").doesNotExist());
 mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"New User\",\"email\":\"new@test.local\",\"password\":\"SecurePass123!\"}")).andExpect(status().isConflict());
 mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"\",\"email\":\"bad\",\"password\":\"short\"}")).andExpect(status().isBadRequest());
 mvc.perform(get("/api/dashboard/properties")).andExpect(status().isUnauthorized());
 mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content("{\"email\":\"owner@househunt.local\",\"password\":\"wrong\"}")).andExpect(status().isUnauthorized());
 }
 @Test void profileFavoritesAndPropertyCrud()throws Exception {
 String owner=login("owner@househunt.local","OwnerDemo123!"),admin=login("admin@househunt.local","AdminDemo123!"),renter=login("renter@househunt.local","RenterDemo123!");
 mvc.perform(put("/api/account/profile").header("Authorization",renter).contentType(MediaType.APPLICATION_JSON).content("{\"phone\":\"9876543210\",\"preferredCity\":\"Pune\"}")).andExpect(status().isOk());
 mvc.perform(get("/api/account/profile").header("Authorization",renter)).andExpect(jsonPath("$.preferredCity").value("Pune"));
 mvc.perform(put("/api/account/favorites/1").header("Authorization",renter)).andExpect(status().isNoContent());
 mvc.perform(get("/api/account/favorites").header("Authorization",renter)).andExpect(jsonPath("$[0].id").value(1));
 mvc.perform(get("/api/account/favorites").header("Authorization",owner)).andExpect(jsonPath("$.length()").value(0));
 mvc.perform(delete("/api/account/favorites/1").header("Authorization",renter)).andExpect(status().isNoContent());
 String body="""
 {"title":"CRUD test home","description":"Nice rental","city":"CrudCity","address":"1 Road","type":"House","price":15000,"bedrooms":2,"bathrooms":1,"area":800,"amenities":"","imageUrl":"","tourUrl":""}
 """;
 var result=mvc.perform(post("/api/properties").header("Authorization",owner).contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isCreated()).andReturn();
 long id=json.readTree(result.getResponse().getContentAsString()).get("id").asLong();
 mvc.perform(patch("/api/admin/properties/"+id).header("Authorization",admin).contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"APPROVED\"}")).andExpect(status().isOk());
 mvc.perform(get("/api/properties").param("city","CrudCity")).andExpect(jsonPath("$.totalElements").value(1));
 mvc.perform(put("/api/properties/"+id).header("Authorization",owner).contentType(MediaType.APPLICATION_JSON).content(body.replace("15000","18000"))).andExpect(status().isOk()).andExpect(jsonPath("$.status").value("PENDING"));
 mvc.perform(get("/api/properties").param("city","CrudCity")).andExpect(jsonPath("$.totalElements").value(0));
 mvc.perform(delete("/api/properties/"+id).header("Authorization",owner)).andExpect(status().isNoContent());
 mvc.perform(get("/api/properties/"+id)).andExpect(status().isNotFound());
 mvc.perform(get("/api/auth/me").header("Authorization",renter+"tampered")).andExpect(status().isUnauthorized());
 mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Injected\",\"email\":\"inject@test.local\",\"password\":\"SecurePass123!\",\"role\":\"ADMIN\"}")).andExpect(status().isBadRequest());
 }
 @Test void dashboardQueriesWorkForEveryRole()throws Exception {
  String admin=login("admin@househunt.local","AdminDemo123!");
  String owner=login("owner@househunt.local","OwnerDemo123!");
  String renter=login("renter@househunt.local","RenterDemo123!");
  mvc.perform(get("/api/dashboard/properties").header("Authorization",admin))
   .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));
  mvc.perform(get("/api/dashboard/properties").header("Authorization",owner))
   .andExpect(status().isOk()).andExpect(jsonPath("$[0].ownerName").value("Priya Sharma"));
  mvc.perform(get("/api/dashboard/properties").header("Authorization",renter))
   .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));
  mvc.perform(get("/api/admin/properties").header("Authorization",admin))
   .andExpect(status().isOk()).andExpect(jsonPath("$.content").isArray())
   .andExpect(jsonPath("$.content[0].status").value("PENDING"));
  mvc.perform(get("/api/admin/users").header("Authorization",admin))
   .andExpect(status().isOk()).andExpect(jsonPath("$.content[0].password").doesNotExist());
  mvc.perform(get("/api/admin/properties").header("Authorization",renter))
   .andExpect(status().isForbidden());
 }
 @Test void hostedFrontendRoutesForwardWithoutExposingProtectedApis()throws Exception{
  for(String path:java.util.List.of("/","/login","/register","/dashboard","/account","/list","/edit/1","/property/1")){
   mvc.perform(get(path)).andExpect(status().isOk()).andExpect(forwardedUrl("/index.html"));
  }
  mvc.perform(get("/api/dashboard/properties")).andExpect(status().isUnauthorized());
  mvc.perform(get("/api/admin/users")).andExpect(status().isUnauthorized());
 }
}