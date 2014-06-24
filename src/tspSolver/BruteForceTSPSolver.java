package tspSolver;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class BruteForceTSPSolver {
	ArrayList<City> cities = new ArrayList<City>();
	ArrayList<Integer> currentPermutation = new ArrayList<Integer>();
	ArrayList<Integer> lowestCostPermutation = new ArrayList<Integer>();
	ArrayList<Integer> tempPermutation = new ArrayList<Integer>();
	double lowestCost = 0.0;
	double currentCost = 0.0;
	int numOfPermutations = 1;
	ParseTSPLIB parser = new ParseTSPLIB();

	public BruteForceTSPSolver() {
	}
	
	public int computeCostOfTour(ArrayList<City> cities, ArrayList<Integer> current_permutation) {
		int costOfTour = 0;
		int numOfCities = cities.size();

		for (int i=0; i < current_permutation.size(); i++){
			int firstCityIndex = i%numOfCities;
			int secondCityIndex = (i+1)%numOfCities;
			double firstCityX = cities.get((current_permutation.get(firstCityIndex)-1)%numOfCities).getCity_x_coord();
			double secondCityX = cities.get((current_permutation.get(secondCityIndex)-1)%numOfCities).getCity_x_coord();
			double firstCityY = cities.get((current_permutation.get(firstCityIndex)-1)%numOfCities).getCity_y_coord();
			double secondCityY = cities.get((current_permutation.get(secondCityIndex)-1)%numOfCities).getCity_y_coord();
			double xDistance = Math.abs(firstCityX - secondCityX);
            double yDistance = Math.abs(firstCityY - secondCityY);
            int costOfLeg = (int) Math.round(Math.sqrt((xDistance*xDistance) + (yDistance*yDistance)));
			costOfTour = costOfTour + costOfLeg;
		}
		return costOfTour;
	}
	
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
	
	public ArrayList<Integer> findNextPermutation (ArrayList<Integer> currentPermutation){
		ArrayList<Integer> newPermutation = new ArrayList<Integer>();
		List<Integer> modifiedSection = new ArrayList<Integer>();
		newPermutation.addAll(currentPermutation);
		
		for (int i=currentPermutation.size()-1; i>0; i--){
			if (currentPermutation.get(i) > currentPermutation.get(i-1)) {
				for (int j=currentPermutation.size()-1; j>(i-1); j--){
					if (currentPermutation.get(j) > currentPermutation.get(i-1)){
						int swapvalue1 = currentPermutation.get(j);
						int swapvalue2 = currentPermutation.get(i-1);
						newPermutation.set(i-1, swapvalue1);
						newPermutation.set(j, swapvalue2);

						for (int k=i; k<newPermutation.size(); k++){
							modifiedSection.add(newPermutation.get(k));
						}
						
						Collections.sort(modifiedSection);

						for (int k=0; k<modifiedSection.size(); k++){
							newPermutation.remove(modifiedSection.get(k));
						}
						for (int k=0; k<modifiedSection.size(); k++){
							newPermutation.add(modifiedSection.get(k));
						}
						return newPermutation;
					}
				}//end inner for loop
				return newPermutation;
			} else {
				
			}//end if check for dip
		}// end outer for loop
		return newPermutation;
	}//end of findNextPermutation function
	
	
	public String solve () {
		long startTime = System.nanoTime(); //start the clock!
		
		//clear arraylists from previous run
		currentPermutation.clear();
		lowestCostPermutation.clear();
		tempPermutation.clear();
		numOfPermutations = 1;
		
		for (int i=1; i<=cities.size(); i++) {
			numOfPermutations = numOfPermutations * i;
		}
		
		//compute initial permutation (must be in ascending order)
		//initial perm = |1|2|3|...|n| where n=size of city arraylist
		for (int i = 1; i <= cities.size(); i++){
			currentPermutation.add(i);
		}
		
		//for debugging
		String solveResults = "First Permutation (#1): ";
		for (int i=0; i < currentPermutation.size(); i++){
			solveResults += currentPermutation.get(i) + " ";
		}
		solveResults += "\n";
		
		currentCost = computeCostOfTour(cities, currentPermutation);
		lowestCost = currentCost;
		lowestCostPermutation.addAll(currentPermutation);
		
		for (int i=2; i<=numOfPermutations; i++){
			tempPermutation.clear();
			tempPermutation.addAll(findNextPermutation(currentPermutation));
			currentPermutation.clear();
			currentPermutation.addAll(tempPermutation);
			currentCost = computeCostOfTour(cities, currentPermutation);
			if (currentCost < lowestCost){
				lowestCostPermutation.clear();
				lowestCost = currentCost;
				lowestCostPermutation.addAll(currentPermutation);
			}

			if (i==numOfPermutations){
				solveResults += "Last Permutation (#" + i + "): ";
				for (int j=0; j< currentPermutation.size(); j++){
					solveResults += currentPermutation.get(j) + " ";
				}
				solveResults += "\n";
			}
		}
		
		solveResults += "Lowest Cost Permutation: ";
		for (int i=0; i<lowestCostPermutation.size(); i++){
			solveResults += lowestCostPermutation.get(i) + " ";
		}
		solveResults += "\nLowest Cost: "+ lowestCost + "\n";
		
		long elapsedTime = System.nanoTime() - startTime;
		
		solveResults += "Elapsed Time: " + TimeUnit.SECONDS.convert(elapsedTime, TimeUnit.NANOSECONDS) + " seconds.\n";
		
		return solveResults;
	}

	public String sendFinalDataForWrite(File tspFile) throws IOException {
		String dataToSave = "";
		dataToSave = "NAME: " + tspFile.getName().replace(".tsp", ".tour") + "\n"
				+ "TYPE: TOUR\n"
				+ "DIMENSION: " + lowestCostPermutation.size() + "\n"
				+ "TOUR_SECTION\n";
		for (int i = 0; i < lowestCostPermutation.size(); i++){
			dataToSave += lowestCostPermutation.get(i) + "\n";
		}
		dataToSave += (lowestCostPermutation.get(0) * -1) + "\n";
		
		dataToSave += "TOUR_COST: " + lowestCost + "\n"; //write cost of final tour to file
		
		parser.writeToFile(tspFile, dataToSave);

		dataToSave = "\nOutput file saved to: " + tspFile.getCanonicalPath().replace(".tsp",".tour") + "\n\n" + dataToSave;
		
		return dataToSave;
	}


}
