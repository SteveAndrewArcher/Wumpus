package wumpus;

import java.util.Random;

public class WumpusBoard {
	public static Tile[][] board;
	public int points = 0;
	public int arrow = 1;
	public boolean deadWumpus = false;
   public boolean foundWumpus = false;
   public boolean maybeFoundWumpus;
   public int safeMoves=0;

   public static KnowledgeBase KB = new KnowledgeBase();
	
	public static void InitializeBoard(){
		board = new Tile[4][4];
		for(int i = 0; i < board.length; i++){
			for(int j = 0; j < board[i].length; j++){
				board[i][j] = new Tile(i, j);
			}
		}
		
		// Agent starts at tile 0,0
		// 0,0 is visited, it is OK
		board[0][0].OK = true;
		board[0][0].V = true;
		board[0][0].A = true;
		
		// Assign tile adjacents
		for(int i = 0; i < 4; i++){
			for(int j = 0; j < 4; j++){
				//System.out.println("i: " + i + " j: " + j);
				if(j-1 > -1)
					board[i][j].Adjacents.add(board[i][j-1]);
				if(j+1 < board[i].length)
					board[i][j].Adjacents.add(board[i][j+1]);
				if(i-1 > -1)
					board[i][j].Adjacents.add(board[i-1][j]);
				if(i+1 < board.length)board[i][j].Adjacents.add(board[i+1][j]);
			}
		}
		int wx = 0;
		int wy = 0;
		Random rand = new Random();
		
		// Place the wumpus
		while((wx == 0 && wy == 0)){
			wx = rand.nextInt(3) + 1;
			wy = rand.nextInt(3) + 1;
			if((wx == 0 && wy == 0) || (wx == 1 && wy == 0) || (wx == 0 && wy == 1) || (wx == 1 && wy == 1)){
				wx = 0;
				wy = 0;
				continue;
			}
			else{
				board[wx][wy].W = true;
				board[wx][wy].SpreadStench();
			}
		}
		System.out.println("The wumpus is at: " + wx + ", " + wy);
		
		int pits = 3;
		int i = 0;
		int px = 0;
		int py = 0;
		
		// Place pits
		while(i < pits){
			px = 0;
			py = 0;
			while(px == 0 && py == 0){
				px = rand.nextInt(3) + 1;
				py = rand.nextInt(3) + 1;
				if((px == 0 && py == 0) || (px == 1 && py == 0) ||(px == 0 && py == 1) || (px == 1 && py == 1) || board[px][py].W || board[px][py].P)
					continue;
				else{
					board[px][py].P = true;
					board[px][py].SpreadBreeze();
					System.out.println("Pit " + (i+1) + " is at: " + px + ", " + py);
					i++;
				}
			}
		}
		
		int gx = 0;
		int gy = 0;
		boolean goldPlaced = false;
		
		// Place the gold
		while(!goldPlaced){
			gx = rand.nextInt(3) + 1;
			gy = rand.nextInt(3) + 1;
			if((gx == 0 && gy == 0) ||(gx == 1 && gy == 1) ||(gx == 1 && gy == 0) || (gx == 0 && gy == 1) ||board[gx][gy].W || board[gx][gy].P){
				continue;
			} else{
				board[gx][gy].G = true;
				goldPlaced = true;
				System.out.println("The gold is at: " + gx + ", " + gy);
			}
		}
      initializeKB();
	}
   
   public static void initializeKB()
   {
      //generate the rules of Wumpus World as definite clauses
      /* Symbols: b = Breeze
                  s = Stench
                  p = Pit
                  w = Wumpus
                  t = sTill air, (the absence of breeze)
                  a = a pleasant Aroma, (the absence of stench)
                  f = solid Floor, (the absence of a pit)
                  c = Crickets, (the absence of a wumpus)
      */
      
      // S & S > Wxy
      for(int row=0; row<4; row++)
      {
         for(int col=0; col<4; col++)
         {
            Clause c = new Clause();
            Tile conTile = board[row][col];
            c.setConclusion("w"+ makeSymbol(conTile.x, conTile.y));
            for(int a=0; a<conTile.Adjacents.size(); a++)
            {
               for(int b=a+1; b<conTile.Adjacents.size(); b++)
               {
                  Tile stench1 = conTile.Adjacents.get(a);
                  Tile stench2 = conTile.Adjacents.get(b);
                  c.addPremise("s"+ makeSymbol(stench1.x, stench1.y));
                  c.addPremise("s"+ makeSymbol(stench2.x, stench2.y));
                  for(int i=0; i<stench1.Adjacents.size(); i++)
                  {
                     for(int j=0; j<stench2.Adjacents.size(); j++)
                     {
                        if(stench1.Adjacents.get(i) == stench2.Adjacents.get(j) && stench1.Adjacents.get(i) != conTile)
                           c.addPremise("c"+makeSymbol(stench1.Adjacents.get(i).x, stench1.Adjacents.get(i).y));
                     }
                  } 
               }               
            }
            KB.addRule(c);
         }
      }
      //Axy > C in all adjacents
      for(int row=0; row<4; row++)
      {
         for(int col=0; col<4; col++)
         {
            Tile premTile = board[row][col];
            for(int a=0; a<premTile.Adjacents.size(); a++)
            {
               Clause c = new Clause();
               c.addPremise("a"+makeSymbol(premTile.x,premTile.y));
               c.setConclusion("c"+makeSymbol(premTile.Adjacents.get(a).x, premTile.Adjacents.get(a).y));
               KB.addRule(c);  
            }
         }
      }
      //Txy > F in all adjacents
      for(int row=0; row<4; row++)
      {
         for(int col=0; col<4; col++)
         {
            Tile premTile = board[row][col];
            for(int a=0; a<premTile.Adjacents.size(); a++)
            {
               Clause c = new Clause();
               c.addPremise("t"+makeSymbol(premTile.x,premTile.y));
               c.setConclusion("f"+makeSymbol(premTile.Adjacents.get(a).x, premTile.Adjacents.get(a).y));
               KB.addRule(c);  
            }
         }
      }
      //Bxy & F & F... > P in last adjacent
      for(int row=0; row<4; row++)
      {
         for(int col=0; col<4; col++)
         {
            Tile premTile = board[row][col];
            for(int a=0; a<premTile.Adjacents.size(); a++)
            {
               Clause c = new Clause();
               c.addPremise("b"+makeSymbol(premTile.x,premTile.y));
               c.setConclusion("p" + makeSymbol(premTile.Adjacents.get(a).x, premTile.Adjacents.get(a).y));
               for(int b=0; b<premTile.Adjacents.size(); b++)
               {
                  if(b!=a)
                     c.addPremise("f"+makeSymbol(premTile.Adjacents.get(b).x,premTile.Adjacents.get(b).y));
                     
               } 
               KB.addRule(c); 
            }
         }
      }
      //Sxy & a & a... > w in last adjacent
      for(int row=0; row<4; row++)
      {
         for(int col=0; col<4; col++)
         {
            Tile premTile = board[row][col];
            for(int a=0; a<premTile.Adjacents.size(); a++)
            {
               Clause c = new Clause();
               c.addPremise("s"+makeSymbol(premTile.x,premTile.y));
               c.setConclusion("w" + makeSymbol(premTile.Adjacents.get(a).x, premTile.Adjacents.get(a).y));
               for(int b=0; b<premTile.Adjacents.size(); b++)
               {
                  if(b!=a)
                     c.addPremise("a"+makeSymbol(premTile.Adjacents.get(b).x,premTile.Adjacents.get(b).y));
                     
               } 
               KB.addRule(c); 
            }
         }
      }


      
      
   }
   
   public static String makeSymbol(int x, int y)
   {
      return(Integer.toString(x)+Integer.toString(y));  
   }

	public void PrintBoard(){
		System.out.println();
		for(int i = 0; i < board.length; i++){
			for(int j = 0; j < board[i].length; j++){
				System.out.print(board[i][j].toString() + " ");
			}
			System.out.println();
		}
	}
   
   public void AgentTurn()
   {
      Tile agentLoc = findAgent();
      if(agentLoc.B)
         KB.addKnown("b"+makeSymbol(agentLoc.x,agentLoc.y));
      else
         KB.addKnown("t"+makeSymbol(agentLoc.x,agentLoc.y));
      if(agentLoc.S)
         KB.addKnown("s"+makeSymbol(agentLoc.x,agentLoc.y));
      else
         KB.addKnown("a"+makeSymbol(agentLoc.x,agentLoc.y));
      KB.addKnown("f"+makeSymbol(agentLoc.x,agentLoc.y));
      KB.addKnown("c"+makeSymbol(agentLoc.x,agentLoc.y));
      //************** Evalulate Adjecent Tiles based on Knowledge Base ***********************
      for(int i=0; i<agentLoc.Adjacents.size(); i++)
      {
         Tile testTile = agentLoc.Adjacents.get(i);
         String testLoc = Integer.toString(testTile.x) + Integer.toString(testTile.y);
         Boolean pit = null;
         Boolean nopit = null;
         Boolean wumpus = null;
         Boolean nowumpus = null;
         String pitquery = "p"+testLoc;
         String nopitquery = "f"+testLoc;
         String wumpusquery = "w"+testLoc;
         String nowumpusquery = "c"+testLoc;
         
         if(KB.entails(pitquery)) //there IS a pit in the adjacent Tile
         {
            pit = true;
            testTile.KP = true;
            KB.addKnown(pitquery);
         }     
         if(KB.entails(nopitquery))
         {
            nopit = true;
            KB.addKnown(nopitquery);
         }
         if(KB.entails(wumpusquery) && !deadWumpus) //there IS a wumpus in the adjacent tile
         {
            wumpus = true;
            testTile.KW = true;
            KB.addKnown(wumpusquery);
         }
         if(KB.entails(nowumpusquery) || deadWumpus)
         {
            nowumpus = true;
            KB.addKnown(nowumpusquery);
         }   
         if(nopit==true && nowumpus==true) //The adjecent tile has neither a pit or a wumpus
         {
            testTile.OK = true;
            safeMoves++;
         }
         if(nopit==false && pit==false) //There may be a pit in the adjacent tile
            testTile.DP = true;
         if(nowumpus==false && wumpus==false) //There may be a wumpus in the adjacent tile
         {
            maybeFoundWumpus = true;
            testTile.DW = true;
         }
      }
      //****************************************************************************************
      
      
      //*************** Find the best tile to move to ******************************************
      
      for(int i=0; i<agentLoc.Adjacents.size(); i++) //if tile is safe and unvisited, move there
      {
         Tile testTile = agentLoc.Adjacents.get(i);
         if(testTile.OK && !testTile.V)
         {
            safeMoves--;
            MoveAgent(testTile.x, testTile.y);
            return;
         }     
      }
      for(int i=0; i<agentLoc.Adjacents.size(); i++) //otherwise, if there's another safe move out there, move back to a visited tile
      {
         Tile testTile = agentLoc.Adjacents.get(i);
         if(testTile.OK && testTile.V && safeMoves > 0)
         {
            MoveAgent(testTile.x, testTile.y);
            return;
         }     
      }
      if(foundWumpus && arrow==1)
      {
         for(int i=0; i<agentLoc.Adjacents.size(); i++) //otherwise, take a shot at the wumpus if avaliable
         {
            Tile testTile = agentLoc.Adjacents.get(i);
            if(shotAtWumpus(agentLoc, testTile))
            {
               ShootArrow(testTile.x, testTile.y);
               return;
            } 
         }
             
      }
      else if(maybeFoundWumpus && arrow==1)
      {
         for(int i=0; i<agentLoc.Adjacents.size(); i++) //otherwise, take a shot at the wumpus if possibly available
         {
            Tile testTile = agentLoc.Adjacents.get(i);
            if(maybeShotAtWumpus(agentLoc, testTile))
            {
               ShootArrow(testTile.x, testTile.y); 
               return;
            } 
         }
      }
      if((foundWumpus || maybeFoundWumpus) && arrow == 1) //otherwise, move until you have a possible shot at wumpus
      {
         for(int i=0; i<agentLoc.Adjacents.size(); i++)
         {
            Tile testTile = agentLoc.Adjacents.get(i);
            if(testTile.OK)
            {
               MoveAgent(testTile.x, testTile.y);
               return;
            }     
         }
      }
      for(int i=0; i<agentLoc.Adjacents.size(); i++)
      {
         Tile testTile = agentLoc.Adjacents.get(i);
         if(!testTile.KP && !testTile.KW && !testTile.V)//as a last resort, move to an unvisited but possibly dangerous tile and hope for the best
         {
            MoveAgent(testTile.x, testTile.y);
            return;
         }
      }
      PrintBoard();
      agentLoc = findAgent();
      if(agentLoc.G)
      {
         win();
      }
      if(agentLoc.P)
      {
         AgentDied();
      }
      if(agentLoc.W)
      {
         AgentDied();
      }
   }
   
   	
	public void MoveAgent(int x, int y){ 
		Tile agentLoc = findAgent();
      agentLoc.A = false;
      board[x][y].A = true;
      board[x][y].V = true;
      points--;
	}
	
	public void AgentDied(){
		// This method is called when the agent comes in contact with the wumpus or falls into a pit. 
		//Game is over
		points -= 1000;
		
		
	}
   
   public void win(){
      points += 1000;
   }
	
	public void ShootArrow(int x, int y){
		if(shotAtWumpus(findAgent(), board[x][y]))
      {
         System.out.println("You killed the wumpus!");
         deadWumpus = true;
      }
      arrow--;
		points -= 10;	
	}
   
   public Tile findAgent()
   {
      for(int i=0; i<board.length; i++)
      {
         for(int j=0; j<board[i].length; j++)
         {
            if (board[i][j].A==true)
               return board[i][j];
         }
      }
      return null;
   }
   
   public boolean shotAtWumpus(Tile firstTile, Tile nextTile)
   {
      if(nextTile.KW)
         return true;
      for(int i=0; i<nextTile.Adjacents.size(); i++)
      {
         Tile testTile = nextTile.Adjacents.get(i);
         if((firstTile.x == testTile.x && nextTile.x == testTile.x) || (firstTile.y == testTile.y && nextTile.y == testTile.y))
         {
            return shotAtWumpus(nextTile, testTile);
         }
      }
      return false;
   }
   
   public boolean maybeShotAtWumpus(Tile firstTile, Tile nextTile)
   {
      if(nextTile.DW)
         return true;
      for(int i=0; i<nextTile.Adjacents.size(); i++)
      {
         Tile testTile = nextTile.Adjacents.get(i);
         if((firstTile.x == testTile.x && nextTile.x == testTile.x) || (firstTile.y == testTile.y && nextTile.y == testTile.y))
         {
            return shotAtWumpus(nextTile, testTile);
         }
      }
      return false;
   }
   



	
}
