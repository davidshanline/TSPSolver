package tspSolver;


import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class ParseTSPLIB {
	
    public ArrayList<City> parseIntoCities(File tspFile, ArrayList<City> cities) throws IOException {
        Scanner input = null;

        try {
        	input = new Scanner(tspFile);

            while (input.hasNextLine()) {
                String temp = input.nextLine().trim();
                if (temp.equals("NODE_COORD_SECTION")){
                	while (input.hasNextLine()){
                		String citytemp = input.nextLine().replaceAll("\\s+", " ").trim();
                    	if (!(citytemp.equals("EOF"))){
                    		String[] citystring = citytemp.split(" ");
                        	cities.add(new City(citystring));
                    	}
                	}
                }
            }
        } finally {
            if (input != null) {
                input.close();
            }
        }
        
        return cities;
    }
    
    public String parseFileContents(File tspFile) throws IOException {
        Scanner input = null;
        String temp = "";

        try {
        	input = new Scanner(tspFile);

            while (input.hasNextLine()) {
                temp += input.nextLine().trim() + "\n";
            }
        } finally {
            if (input != null) {
                input.close();
            }
        }
        
        return temp;

    }
    
    public void writeToFile(File tspFile, String dataToSave) throws IOException {
    	String outputfilename = tspFile.getCanonicalPath().replace(".tsp",".tour");
		File targetfile = new File(outputfilename);
		
		PrintWriter output = new PrintWriter(targetfile);

		output.println(dataToSave);
		
		output.close();
    	
    }
}
