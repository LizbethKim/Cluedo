package cluedo;

public class AStar implements Comparable<AStar>{

	private AStar parent;
    private int length;
    private int heuristic;
    private Coordinate coords;
    
    public AStar(AStar parent, int length, int heuristic, Coordinate coords){
    	this.parent = parent;
    	this.length = length;
    	this.heuristic = heuristic;
    	this.coords = coords;
    }
    
    public Coordinate getCoords(){
    	return coords;
    }
    
    public int getLength(){
    	return length;
    }
    
    public int getHeuristic(){
    	return heuristic;
    }

	@Override
	public int compareTo(AStar o) {
		if (!(o instanceof AStar)) throw new UnsupportedOperationException();
		return this.heuristic-((AStar) o).getHeuristic();
	}

	public AStar getParent() {
		return parent;
	}
}