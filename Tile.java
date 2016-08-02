package wumpus;

import java.util.ArrayList;

public class Tile {
	public int x;
	public int y;
	
	public ArrayList<Tile> Adjacents = new ArrayList<Tile>();
	
	public boolean A = false; // Agent
	public boolean B = false; // Breeze
	public boolean G = false; // Gold glitter
	public boolean P = false; // Pit
	public boolean S = false; // Stench
	public boolean W = false; // Wumpus
	
	public boolean OK = false;// Safe tile
	public boolean V = false; // Visitied
	public boolean DP = false; // Possible danger. The tile might have a pit.
   public boolean DW = false; // Possible danger. The tile might have a wumpus.
   public boolean KP = false; // the agent has determined there's a pit in this tile.
   public boolean KW = false; // the agent has determined there's a wumpus in this tile.
   
	public Tile(int i, int j){
		this.x = i;
		this.y = j;
		this.A = false;
		this.B = false;
		this.G = false;
		this.P = false;
		this.S = false;
		this.W = false;
		this.OK = false;
		this.V = false;
	}
	
	public void SpreadStench(){
		System.out.println(Adjacents.size());
		for(int i = 0; i < Adjacents.size(); i++){
			Adjacents.get(i).S = true;
		}
	}
	
	public void SpreadBreeze(){
		for(int i = 0; i < Adjacents.size(); i++){
			Adjacents.get(i).B = true;
		}
	}
	
	@Override
	public String toString(){
		String t = "";
		if(this.A){
			t += "A";
		} 
		if(this.B){
			t += "B";
		}
		if(this.G){
			t += "G";
		}
		if(this.P){
			t += "P";
		}
		if(this.S){
			t += "S";
		}
		if(this.V){
			t += "V";
		}
		if(this.W){
			t += "W";
		}
		if(this.OK){
			t += "k";
		}
		if(!this.A && !this.B && !this.G && !this.OK && !this.P && !this.S && !this.V && !this.W )
			t += "X";
		if(t.length() == 1)
			t = " " + t + " ";
		if(t.length() == 2)
			t = t + " ";
		return t;
	}
}
