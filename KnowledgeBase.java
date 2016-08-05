package wumpus;
import java.util.*;
public class KnowledgeBase
{
   private ArrayList<Clause> rules = new ArrayList<Clause>();
   private ArrayList<String> knownTrue = new ArrayList<String>();   
   
   
   public void addRule(Clause c)
   {
      rules.add(c);
   }
   
   public void addKnown(String symbol)
   {
      knownTrue.add(symbol);
   }
   
   public boolean entails(String query)
   {
      int[] count = new int[rules.size()];
      for(int i = 0; i < rules.size(); i++)
      {
         count[i] = rules.get(i).numPremises();
      }
      ArrayList<String> inferred = new ArrayList<String>();
      ArrayList<String> agenda = new ArrayList<String>();
      for(int i = 0; i < knownTrue.size(); i++)
      {
         agenda.add(knownTrue.get(i));
      }
      while(agenda.size()>0)
      {
         String p = agenda.remove(0);
         if(p == query)
            return true;
         if(!inferred.contains(p))
         {
            inferred.add(p);
            for(int i = 0; i < rules.size(); i++)
            {
               if(rules.get(i).containsPremise(p))
               {
                  count[i]--;
                  if(count[i] == 0)
                  {
                     agenda.add(rules.get(i).getConclusion());
                     knownTrue.add(rules.get(i).getConclusion());
                  }
               }
            }
         }
      }
      return false;
   }
}