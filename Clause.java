package wumpus;
import java.util.*;
public class Clause
{
	private ArrayList<String> premises = new ArrayList<String>();
	private String conclusion;

	public void addPremise(String symbol)
	{
		premises.add(symbol);
	}
	
	public void setConclusion(String symbol)
	{
		this.conclusion = symbol;
	}
	
	public int numPremises()
	{
		return premises.size();
	}
	
	public boolean containsPremise(String p)
	{
		return premises.contains(p);
	}
   
	public String getConclusion()
	{
		return this.conclusion;
	}
}