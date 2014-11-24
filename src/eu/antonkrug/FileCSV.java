package eu.antonkrug;

import java.io.BufferedReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * FileCSV class
 * 
 * @author Anton Krug
 * 
 */

public class FileCSV {

	public final static Boolean	VERBOSE	= true;

	private String							moviesFilename;
	private String							ratingsFilename;

	public FileCSV(String moviesFilename, String ratingsFilename) {
		this.moviesFilename = moviesFilename;
		this.ratingsFilename = ratingsFilename;
	}

	public void loadMovies() {

		BufferedReader br = null;
		String line = "";

		try {

			br = new BufferedReader(new FileReader(this.moviesFilename));
			while ((line = br.readLine()) != null) {

				String[] data = line.split(",");

				int key = -1;
				String name = "Not given";
				int year = -1;
				String genre = "Not categorized";

				if (VERBOSE && (data.length == 0 || data.length > 4))
					System.out.println("Wrong line, but still we can continue parsing: " + line);

				try {
					if (data.length > 0) key = Integer.parseInt(data[0]);
					if (data.length > 2) year = Integer.parseInt(data[2]);
				} catch (NumberFormatException e) {
					if (VERBOSE) System.out.println("Wrong numbers in line, but we can continue: " + line);
				}

				if (data.length > 1) name = data[1];
				if (data.length > 3) genre = data[3];

//				System.out.println(key + " name=" + name + " year=" + year + " gengre="	+ genre);
				DB.obj().addMovie(key, name, year, genre);

			}

		} catch (FileNotFoundException e) {
			System.out.println("File is missing!");
		} catch (IOException e) {
			// different execption
			e.printStackTrace();
		} finally {
			// when done, try to close file
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if (VERBOSE) System.out.println("Movies loaded");
	}

	public void loadRatings() {

		BufferedReader br = null;
		String line = "";

		try {

			br = new BufferedReader(new FileReader(this.ratingsFilename));
			
			Boolean userLine=true;
			User lastUser=null;
			
			while ((line = br.readLine()) != null) {
				String[] data = line.split(",");

				if (VERBOSE && data.length == 0)
					System.out.println("Wrong line, but still we can continue parsing: " + line);

				if (userLine) {
					userLine=false;
					
					String signName = "N/A";
					String firstName = "N/A";
					String lastName = "N/A";
					String password = "secret";

					if (data.length > 0) signName = data[0];
					if (data.length > 1) firstName = data[1];
					if (data.length > 2) lastName = data[2];
					if (data.length > 3) password = data[3];
								
					lastUser = DB.obj().addUser(signName,firstName,lastName,password);
				
				} else {
					userLine=true;
					
					if (lastUser!=null) {
						for (int i=0;i<data.length;i++) {
							
							Rating rating = Rating.getRating(data[i]);
							
							//add all SEEN movies
							if (rating != Rating.NOT_SEEN) {
								lastUser.addRating(i+1, rating.getValue());
							}
						}
					}
				}

			}

		} catch (FileNotFoundException e) {
			System.out.println("File is missing!");
		} catch (IOException e) {
			// different execption
			e.printStackTrace();
		} finally {
			// when done, try to close file
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if (VERBOSE) System.out.println("Ratings loaded");
	}
	
}
