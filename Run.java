package wumpus;
import java.util.Scanner;

public class Run {

	public static void main(String[] args) {
      Scanner sc = new Scanner(System.in);
		WumpusBoard wb = new WumpusBoard();
		wb.InitializeBoard();
		wb.PrintBoard();
      while(wb.points>-1000 && wb.points<=0)
      {
         //System.out.print("press ENTER for the next turn.");
         //sc.nextLine();
         wb.AgentTurn();
         
      }
      
      if(wb.findAgent().P)
      {
         System.out.println("You fell in a pit! Game Over!");
      }
      if(wb.findAgent().W)
      {
         System.out.println("You were eaten by the Wumpus! Game Over!");
      }
      if(wb.findAgent().G)
      {
         System.out.println("Congratulations, you found the gold! Game Over.");
      }
      
	}
}
