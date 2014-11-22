package eu.antonkrug;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Database {

	public final static Boolean	VERBOSE	= true;

	public static void main(String[] args) {
		Database app = new Database();
		app.importCSV();
	}

	public ArrayList<Movie>					movies;
	public ArrayList<MovieCategory>	categories;

	public Database() {
		this.movies = new ArrayList<Movie>();
		this.categories = new ArrayList<MovieCategory>();
	}

	public void addMovie(String name) {
		MovieCategory category = new MovieCategory(name);
		int index = this.categories.indexOf(category);

		if (index >= 0) {
			this.categories.get(index).incTimesUsed();
		} else {
			this.categories.add(new MovieCategory(name));
			Collections.sort(this.categories, MovieCategory.BY_NAME);
		}
	}

	public void importCSV() {

		String csvFile = "films_fx.csv";
		BufferedReader br = null;
		String line = "";

		try {

			br = new BufferedReader(new FileReader(csvFile));
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

				System.out.println(" Movie " + key + " name=" + name + " , year=" + year + " , gengre="
						+ genre);

			}

		} catch (FileNotFoundException e) {
			System.out.println("File is missing.");
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
		if (VERBOSE) System.out.println("Done");
	}

}
