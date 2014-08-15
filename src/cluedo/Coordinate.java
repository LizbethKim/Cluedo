package cluedo;

public class Coordinate {

	private int x, y;
	
	public Coordinate(int x, int y){
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	/**
	 * Adds two coordinates together and returns the result.
	 * @param one
	 * @param two
	 * @return
	 */
	public static Coordinate addCoords(Coordinate one, Coordinate two) {
		return new Coordinate(one.x + two.x, one.y + two.y);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Coordinate other = (Coordinate) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
	
	public String toString() {
		return "(" + x + ", " + y + ")";
		
	}
}