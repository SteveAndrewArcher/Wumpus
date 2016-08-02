package wumpus;

public class Model
{
   private TileModel[][] board = new TileModel[4][4];
   
   public Model()
   {
      for(int row = 0; row < board.length; row++){
         for(int col = 0; col < board[row].length; col++){
            board[row][col] = new TileModel();
         }
      }
   }
   
   public boolean get(char feature, int row, int col)
   {
      return board[row][col].get(feature);   
   }
   
   public void set(char feature, int row, int col, boolean value)
   {
      board[row][col].set(feature, value);
   }
}