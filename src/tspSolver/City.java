/*
 * File: City.java
 * Purpose: base class for creating city objects
 * 
 * @author David Shanline
 * @author Sean Fast
 */

package tspSolver;


public class City {
	private int city_id = 0;
	private double city_x_coord = 0.0;
	private double city_y_coord = 0.0;
	private boolean visited = false;
	
	//default base constructor
	public City() {
		this.city_id = 0;
		this.city_x_coord = 0.0;
		this.city_y_coord = 0.0;
		this.visited = false;
	}
	
	//city constructor using a string array of cities with given attributes
	public City(String[] newCity){
		this.city_id = Integer.parseInt(newCity[0]);
		this.city_x_coord = Double.parseDouble(newCity[1]);
		this.city_y_coord = Double.parseDouble(newCity[2]);
	}
	
	public Integer getCity_id() {
		return city_id;
	}

	public Double getCity_x_coord() {
		return city_x_coord;
	}

	public Double getCity_y_coord() {
		return city_y_coord;
	}
	
	public Boolean getVisitedStatus() {
		return visited;
	}
	
	public void setVisitedStatus(boolean status) {
		visited = status;
	}
	
}

