import java.util.*;
public class PropositionalStatement
{
   private String sentence;
   private Stack<Boolean> stack = new Stack<Boolean>();    
   
   public PropositionalStatement(String sentence)
   {
      this.sentence = sentence;   
   }
   
   public String getSentence()
   {
      return this.sentence;
   }
   
   public void add(PropositionalStatement query)
   {
      this.sentence += query.getSentence();
   }
   
   public boolean evaluate(Model m)
   {
      
      char term, col, row;
      boolean value, value2;
      for(int i = 0; i < sentence.length(); i++)
      {
         term = sentence.charAt(i);
         if(term == 'b' || term == 'p' || term == 's' || term == 'w')
         {
            i++;
            col = sentence.charAt(i);
            i++;
            row = sentence.charAt(i);
            value = m.get(term, row, col);
            stack.push(value);
         }
         if(term == '-')
         {
            value = stack.pop();
            stack.push(!value);
         }
         if(term == '&')
         {
            value = stack.pop();
            value2 = stack.pop();
            stack.push(value && value2);
         }
         if(term == '|')
         {
            value = stack.pop();
            value2 = stack.pop();
            stack.push(value || value2);
         }
         if(term == '>')
         {
            value = stack.pop();
            value2 = stack.pop();
            stack.push(!(value2 && !value));   
         }
         if(term == '=')
         {
            value = stack.pop();
            value2 = stack.pop();
            stack.push(value==value2);
         }
      }
      return stack.pop();   
   }

}