public class TileModel
{
   private boolean breeze, stench, pit, wumpus;
   
   public void set(char feature, boolean value)
   {
      switch(feature){
         case 'b':
            this.breeze = value;
            break;
         case 's':
            this.stench = value;
            break;
         case 'p':
            this.pit = value;
            break;
         case 'w':
            this.wumpus = value;
            break;
      }
   }
   
   public boolean get(char feature)
   {
      switch(feature){
         case 'b':
            return this.breeze;
         case 's':
            return this.stench;
         case 'p':
            return this.pit;
         case 'w':
            return this.wumpus;
      }
      return false;
   }
}