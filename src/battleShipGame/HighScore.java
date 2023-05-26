package battleShipGame;



/* Score Class */
public class HighScore implements Comparable<HighScore> {
	/* Private variables */
	private String name  = null;
	private int turns    = -1;
	
	
	
	/* Constructors */
	public HighScore()                         { }
	public HighScore(String name, int turns)   { setScore(name, turns); }
	
	
	
	/* Getters and setters */
	public String getName()    { return name;  }
	public int    getTurns()   { return turns; }
	
	public void setScore(String name, int turns) { // Setting a score, requires a valid name and positive amount of turns
		if (name == null || name.length() == 0 || turns < 1)
			return;
		
		this.name  = name;
		this.turns = turns;
	}
	
	
	
	/* Public Methods */
	public boolean isSet() { return name != null; } // Check if the score is set by having it's name being set
	
	public void clear() { // Clear out the score, and revert back to the default values
		name  = null;
		turns = -1;
	}
	
	@Override
	public int compareTo(HighScore score) { // For comparing with sorting (ArrayList collections)
		int compareTurns = score.getTurns();
		return this.turns - compareTurns; // Ascending Order
	}
}