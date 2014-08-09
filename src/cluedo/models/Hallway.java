package cluedo.models;

import cluedo.Coordinate;


public class Hallway implements Square{

	private Coordinate coords;
	
	public Hallway(Coordinate coords){
		this.coords = coords;
	}
	
	public Coordinate getCoords(){
		return coords;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((coords == null) ? 0 : coords.hashCode());
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
		Hallway other = (Hallway) obj;
		if (coords == null) {
			if (other.coords != null)
				return false;
		} else if (!coords.equals(other.coords))
			return false;
		return true;
	}
}
