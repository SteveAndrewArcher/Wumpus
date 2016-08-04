package wumpus;

import java.util.*;
public class PropStatement
{
   private String sentence;
   private Stack<Boolean> stack = new Stack<Boolean>();    
   
   public PropStatement(String sentence)
   {
      this.sentence = sentence;   
   }
   
   public String getSentence()
   {
      return this.sentence;
   }
   
   public void add(PropStatement query)
   {
      this.sentence = this.sentence + query.getSentence() + "&";
   }
   
   public boolean entails(PropStatement query)
   {
      TreeSet<String> symbols = new TreeSet();
      for(int i=0; i<this.sentence.length(); i++)
      {  
         if(sentence.charAt(i) == 'b' || sentence.charAt(i) == 'p' || sentence.charAt(i) == 'w' || sentence.charAt(i) == 's')
         {
            symbols.add(sentence.substring(i, i+3));
            i+=2;
         }
      }
      for(int i=0; i<query.getSentence().length(); i++)
      {  
         if(query.getSentence().charAt(i) == 'b' || query.getSentence().charAt(i) == 'p' || sentence.charAt(i) == 'w' || sentence.charAt(i) == 's')
         {
            symbols.add(sentence.substring(i, i+3));
            i+=2;
         }
      } 
      Model m = new Model();
      String[] symbolsArray = symbols.toArray(new String[symbols.size()]);
      return checkAll(query, symbolsArray, m);  
   }
   
   public boolean checkAll(PropStatement query, String[] symbols, Model m)
   {
      if(symbols.length==0)
      {
         if(this.evaluate(m))
         {
            return query.evaluate(m);
         }
         else
         {
            return true;
         }
      }
      else
      {
         String nextSymbol = symbols[0];
         String[] rest;
         if(symbols.length > 1)
         {
            rest = Arrays.copyOfRange(symbols, 1, symbols.length);
         }
         else
         {
            rest = new String[0];
         }
         Model trueModel = m.clone();
         Model falseModel = m.clone();
         char feature = nextSymbol.charAt(0);
         int row = Character.getNumericValue(nextSymbol.charAt(1));
         int col = Character.getNumericValue(nextSymbol.charAt(2));
         trueModel.set(feature, row, col, true);
         falseModel.set(feature, row, col, false);
         return(checkAll(query, rest, trueModel) && checkAll(query, rest, falseModel));
      }
   }

   //same as True? method from book/slides
   public boolean evaluate(Model m)
   {
      
      char term;
      int col, row;
      boolean value, value2;
      for(int i = 0; i < sentence.length(); i++)
      {
         term = sentence.charAt(i);
         if(term == 'b' || term == 'p' || term == 's' || term == 'w')
         {
            i++;
            col = Character.getNumericValue(sentence.charAt(i));
            i++;
            row = Character.getNumericValue(sentence.charAt(i));
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