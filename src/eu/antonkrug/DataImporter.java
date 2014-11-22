package eu.antonkrug;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class DataImporter {

	public final static Boolean	VERBOSE	= true;

	public static void main(String[] args) {
		DataImporter obj = new DataImporter();
		obj.run();
	}

	public void run() {

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

				System.out
						.println(" Movie "+key+" name=" + name + " , year=" + year + " , gengre=" + genre);

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

		System.out.println("Done");
	}

}
