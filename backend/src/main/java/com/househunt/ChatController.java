package com.househunt;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
@RestController @RequestMapping("/api/chat")
class ChatController {
 final Chats chats;final Properties properties;final AuthController auth;final AiService ai;final ListingEmbeddings embeddings;
 final ConcurrentHashMap<Long,Long> lastRequest=new ConcurrentHashMap<>();
 ChatController(Chats c,Properties p,AuthController a,AiService ai,ListingEmbeddings embeddings){chats=c;properties=p;auth=a;this.ai=ai;this.embeddings=embeddings;}
 @GetMapping List<ChatView> history(@AuthenticationPrincipal Jwt jwt){return chats.findTop20ByUserIdOrderByCreatedAtDesc(auth.current(jwt).id).stream().map(ChatView::of).toList();}
 @PostMapping @Transactional ChatView ask(@Valid @RequestBody ChatRequest r,@AuthenticationPrincipal Jwt jwt)throws Exception{
 User u=auth.current(jwt);long now=System.currentTimeMillis();
 lastRequest.compute(u.id,(id,previous)->{if(previous!=null && now-previous<3000)throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,"Please wait a few seconds");return now;});
 String question=r.message().toLowerCase(Locale.ROOT);
 var all=properties.search(PropertyStatus.APPROVED,"","",BigDecimal.ZERO,new BigDecimal("100000000"),"",PageRequest.of(0,100)).getContent();
 var ranked=embeddings.retrieve(question,all);
 String context=ranked.stream().map(p->"#"+p.id+" "+p.title+" | "+p.city+" | INR "+p.price+"/month | "+p.bedrooms+" beds | "+p.amenities).reduce("",(a,b)->a+b+"\n");
 String fallback=ranked.isEmpty()?"No approved rentals are currently available. Try again after owners publish listings.":"Here are approved rentals to explore:\n"+context+"\n\nOpen a listing to request your dates. This is database search mode; configure LLM_API_KEY for conversational answers.";
 ChatMessage m=new ChatMessage();m.user=u;m.question=r.message();m.answer=ai.answer(r.message(),context,fallback).get(40,TimeUnit.SECONDS);return ChatView.of(chats.save(m));
 }
}