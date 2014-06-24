/*
 * File: NearestNeighborTSPSolver.java
 * Purpose: class used to solve the traveling sales person problem
 *          using the greedy nearest neighbor algorithm
 *          
 * @author David Shanline
 * @author Sean Fast
 */

package tspSolver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class NearestNeighborTSPSolver {
	ArrayList<City> cities = new ArrayList<City>();
	ArrayList<ArrayList<Double>> legLengthMatrix = new ArrayList<ArrayList<Double>>();
	ArrayList<City> bestTour = new ArrayList<City>();
	double bestTourCost = 0.0;
	ParseTSPLIB parser = new ParseTSPLIB();
	
	/*
	 * Function: createLegLengthMatrix
	 * Purpose: Creates an adjacency matrix using a two dimensional ArrayList
	 *          that stores the weight of each eadge between each node.
	 * Inputs: ArrayList<City> cities - an ArrayList containing all of the city objects
	 * Outputs: ArrayList<ArrayList<Double>> table - the adjacency matrix of edge weights
	 */
	public ArrayList<ArrayList<Double>> createLegLengthMatrix (ArrayList<City> cities) {
		ArrayList<ArrayList<Double>> table = new ArrayList<ArrayList<Double>>();
		for (int i=0; i<cities.size(); i++){
			ArrayList<Double> row = new ArrayList<Double>();
			for (int j=0; j<cities.size(); j++){
				if (i == j){
					row.add(-1.0d);
				}
				else {
					double xDistance = cities.get(i).getCity_x_coord() - cities.get(j).getCity_x_coord();
					double yDistance = cities.get(i).getCity_y_coord() - cities.get(j).getCity_y_coord();
					double costOfLeg = Math.sqrt((xDistance*xDistance) + (yDistance*yDistance));
					row.add(costOfLeg);
				}
			}
			table.add(row);
		}
		return table;
	}
	
	/*
	 * Function: parseNewFile
	 * Purpose: parses the given tsp file into city objects
	 * Inputs: tspFile - tsp file provided by the user
	 * Outputs: fileContents - parsed contents of the tsp file
	 */
	public String parseNewFile (File tspFile) {
		//parse tsplib file into arraylist of city objects
		String fileContents = "File is empty.\n";
		
		//clear cities from previous run
		if (cities.size() > 0){
			cities.clear();
		}

		try {
			fileContents = parser.parseFileContents(tspFile);
			cities = parser.parseIntoCities(tspFile, cities);
		} catch (IOException e) {
			e.printStackTrace();
		}

		//return string of contents to stat window
		return fileContents;
	}
	
	/*
	 * Function: solve
	 * Purpose: finds the shortest tour for each starting city using
	 *          the greedy nearest neighbor algorithm
	 * Inputs: none
	 * Outputs: A string with the results for output to a file and gui window
	 */
	public String solve () {
		//declare and initialize variables
		long startTime = System.nanoTime(); //start the clock!
		ArrayList<City> currentTour = new ArrayList<City>();
		City currentLocation = new City();
		int currentCityID = -1;
		int closestNeighbor = -1;
		double edgeDistance = 0.0;
		double currentTourCost = 0.0;
		double shortestDistance = -1.0;
		String solveResults = "";
		
		//clear fields from last run if necessary for continuous operation
		bestTourCost = 0.0;
		bestTour.clear();
		legLengthMatrix.clear();
		
		legLengthMatrix = createLegLengthMatrix(cities); //compute distance adjacency matrix
		
		for (City startingCity : cities) {  //for loop to use each city as the starting city
			for (City tempCity : cities){
				tempCity.setVisitedStatus(false); //set all cities to unvisited initially
			}
			currentTour.clear();
			startingCity.setVisitedStatus(true);
			currentTour.add(startingCity);
			currentTourCost = 0.0;
			shortestDistance = -1.0;
			currentLocation = startingCity;
			while(checkForUnvisitedCities(cities) == true){  //keep checking for the next nearest neighbor
				                                             //as long as there is still an unvisited city
				currentCityID = currentLocation.getCity_id();
				for (int i=0; i<cities.size(); i++) {
					if (cities.get(i).getVisitedStatus() == false) {  //if the city hasn't been visited,
						                                              //find its edge weight
						edgeDistance = legLengthMatrix.get(currentCityID-1).get(i);
						if (shortestDistance == -1.0){
							shortestDistance = edgeDistance;
							closestNeighbor = i;
						}
						if (edgeDistance < shortestDistance){
							shortestDistance = edgeDistance;
							closestNeighbor = i;
						}
					}
				}
				//visit the closest neighbor by setting its visited status to true
				cities.get(closestNeighbor).setVisitedStatus(true);
				//add the closest neighbor to the toure
				currentTour.add(cities.get(closestNeighbor));
				//keep a running total of the tour cost
				currentTourCost+=shortestDistance;
				//make your current location the closest neighbor
				currentLocation = cities.get(closestNeighbor);
				shortestDistance = -1.0;
			}
			//add the distance from the last city in the tour back to the starting city
			currentTourCost+=legLengthMatrix.get(currentLocation.getCity_id()-1).get(startingCity.getCity_id()-1); 
			if (bestTourCost == 0.0){
				bestTourCost = currentTourCost;
				bestTour.clear();
				bestTour.addAll(currentTour);
			}
			if (bestTourCost > currentTourCost){
				bestTourCost = currentTourCost;
				bestTour.clear();
				bestTour.addAll(currentTour);
			}
			//build string for log window of every city in tour and its cost
			solveResults += "Starting City: " + startingCity.getCity_id() + "\n";
			solveResults += "Tour: ";
			for (int i=0; i<currentTour.size(); i++) {
				solveResults += currentTour.get(i).getCity_id() + " ";
			}
			solveResults += "\n";
			solveResults += "Cost of Tour: " + currentTourCost;
			solveResults += "\n\n";
		}
		//append lowest costing tour at end of string when done for log window
		solveResults += "Lowest cost tour with greedy algorithm: ";
		for (int i=0; i < bestTour.size(); i++){
			solveResults += bestTour.get(i).getCity_id() + " ";
		}
		solveResults += "\n";
		solveResults += "Cost of tour: " + bestTourCost;
		solveResults += "\n";
		
		long elapsedTime = System.nanoTime() - startTime;
		
		solveResults += "Elapsed Time: " + TimeUnit.SECONDS.convert(elapsedTime, TimeUnit.NANOSECONDS) + " seconds.\n";
		
		return solveResults;
	}
	
	/*
	 * Function: sendFinalDataForWrite
	 * Purpose: sends the final results to the output file
	 * Inputs: tspFile - the tsp file provided by the user
	 * Outpus: dataToSave - string containing the solve results
	 */
	public String sendFinalDataForWrite(File tspFile) throws IOException {
		String dataToSave = "";
		dataToSave = "NAME: " + tspFile.getName().replace(".tsp", ".tour") + "\n"
				+ "TYPE: TOUR\n"
				+ "DIMENSION: " + bestTour.size() + "\n"
				+ "TOUR_SECTION\n";
		for (int i = 0; i < bestTour.size(); i++){
				dataToSave += bestTour.get(i).getCity_id() + "\n";
		}
		
		dataToSave += "-1\n"; //append -1 to end of tour in log only
		
		dataToSave += "TOUR_COST: " + bestTourCost + "\n"; //write cost of final tour to file
		
		parser.writeToFile(tspFile, dataToSave);

		dataToSave = "\nOutput file saved to: " + tspFile.getCanonicalPath().replace(".tsp",".tour") + "\n\n" + dataToSave; //show output file info in log
		
		return dataToSave;
	}
	
	/*
	 * Function: checkForUnvisitedCities
	 * Purpose: checks the list of city objects to see if any of them have not been visited.
	 * Inputs: ArrayList<City> cities - an array list of all city objects
	 * Outputs: boolean indicating if there were any unvisited cities found
	 */
	public Boolean checkForUnvisitedCities(ArrayList<City> cities) {
		for (City s : cities){
			if (s.getVisitedStatus() == false) {
				return true;
			}
		}
		return false;
	}

}
