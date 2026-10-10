package com.househunt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
/** Serve React Router entry points when the production jar includes the frontend build. */
@Controller
class SpaController {
 @GetMapping({"/","/login","/register","/dashboard","/account","/list","/edit/{id}","/property/{id}"})
 String page(){return "forward:/index.html";}
}