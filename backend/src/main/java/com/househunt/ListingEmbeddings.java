package com.househunt;
import org.springframework.stereotype.Component;
import java.util.*;
/** Local feature-hashing embeddings: reproducible vector search without a paid embedding API. */
@Component
class ListingEmbeddings {
 double[] embed(String text){
 double[] v=new double[256];
 for(String word:text.toLowerCase(Locale.ROOT).split("\\W+")){
 if(word.length()<3 || Set.of("the","and","for","with","find","home","want").contains(word))continue;
 int h=word.hashCode();v[Math.floorMod(h,v.length)]+=1;
 }
 double norm=Math.sqrt(Arrays.stream(v).map(x->x*x).sum());
 if(norm>0)for(int i=0;i<v.length;i++)v[i]/=norm;
 return v;
 }
 double similarity(double[] a,double[] b){double s=0;for(int i=0;i<a.length;i++)s+=a[i]*b[i];return s;}
 List<Property> retrieve(String query,List<Property> candidates){
 double[] vector=embed(query);
 return candidates.stream().sorted(Comparator.comparingDouble((Property p)->similarity(vector,embed(p.city+" "+p.title+" "+p.description+" "+p.type+" "+p.amenities))).reversed()).limit(5).toList();
 }
}