package cluedo;

import cluedo.models.Square;


public class AStar {

	private AStar parent;
    private int length;
    private int heuristic;
    private Square square;
    
    public AStar(AStar parent, int length, int heuristic, Square square){
    	this.parent = parent;
    	this.length = length;
    	this.heuristic = heuristic;
    	this.square = square;
    }
}
