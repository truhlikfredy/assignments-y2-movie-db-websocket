package eu.antonkrug;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * DB Input Output class is focused on loading and saving data from/to files in
 * three different formats: CVS, XML and bytestreams (DAT extension).
 * 
 * @author Anton Krug
 * 
 */
public class DBInputOutput implements Serializable {

	/**
	 * Generate new ID if you will change anything in this class, if you change
	 * stuff often then comment it out and let the compiler generate one for you
	 * which will change automaticly if you will do any modifications to the class
	 */
	private static final long				serialVersionUID	= -923545324660029399L;

	public final static Boolean			VERBOSE						= true;

	public ArrayList<MovieGenre>		genres;
	public Boolean									genresDirty;
	public HashMap<Integer, Movie>	movies;
	public ArrayList<User>					users;

	/**
	 * Will detect filenames for CSV and call calls on both of them.
	 * @param fileName
	 */
	public Boolean loadCVS(String fileName) {

		// detect base and extension of filename
		String base = "";
		String extension = "";

		int i = fileName.lastIndexOf('.');
		int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));

		if (i > p) {
			base = fileName.substring(0, i);
			extension = fileName.substring(i + 1);
		} else {
			base = fileName;
		}

		if (extension.toLowerCase().equals("csv")) {
			
			// load both CVS's
			this.loadCVSfile(base + "." + extension);
			this.loadCVSfile(base + "-users." + extension);
			
		} else {
			if (VERBOSE) System.out.println("Not CSV extension. Not loading the file");
			return false;
		}
		return true;
	}

	private Boolean loadCVSfile(String fileName) {

		BufferedReader br = null;
		String line = "";

		try {

			br = new BufferedReader(new FileReader(fileName));

			Boolean userLine = true;
			User lastUser = null;
			Boolean detectFile = true;
			Boolean movies = false;

			while ((line = br.readLine()) != null) {
				String[] data = line.split(",");

				if (VERBOSE && data.length == 0)
					System.out.println("Wrong line, but still we can continue parsing: " + line);

				// detect what type of file we are opening by the number of cols
				if (detectFile) {
					if (data.length > 4)
						movies = false;
					else movies = true;

					if (VERBOSE) {
						if (movies)
							System.out.println("Reading movies from file: " + fileName);
						else System.out.println("Reading users & ratings from file: " + fileName);
					}
					detectFile = false;
				}

				if (movies) {
					// parsing Movies file

					int key = -1;
					String name = "Not given";
					int year = -1;
					String genre = "Not categorized";

					if (VERBOSE && data.length > 4)
						System.out.println("Wrong line, but still we can continue parsing: " + line);

					if (data.length > 0) key = toInt(data[0]);
					if (data.length > 1) name = data[1];
					if (data.length > 2) year = toInt(data[2]);
					if (data.length > 3) genre = data[3];

					DB.obj().addMovie(key, name, year, genre);

				} else {

					if (userLine) {
						// parsing Ratings file and a user line
						userLine = false;

						String signName = "N/A";
						String firstName = "N/A";
						String lastName = "N/A";
						String password = "secret";

						if (data.length > 0) signName = data[0];
						if (data.length > 1) firstName = data[1];
						if (data.length > 2) lastName = data[2];
						if (data.length > 3) password = data[3];

						lastUser = DB.obj().addUser(signName, firstName, lastName, password);

					} else {
						// parsing Ratings file and a ratings line
						userLine = true;

						if (lastUser != null) {
							for (int i = 0; i < data.length; i++) {

								Rating rating = Rating.getRating(data[i]);

								// add all SEEN movies
								if (rating != Rating.NOT_SEEN) {
									lastUser.addRating(i + 1, rating.getValue());
								}
							}
						}
					}
				}

			}

		} catch (FileNotFoundException e) {
			System.out.println("File is missing!");
			return false;
		} catch (IOException e) {
			// different execption
			e.printStackTrace();
			return false;
		} finally {
			// when done, try to close file
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					System.out.println("Can't close the file!");
					e.printStackTrace();
				}
			}
		}
		if (VERBOSE) System.out.println("File loaded.");
		return true;		
	}

	public Boolean loadDAT(String fileName) {
		File file = new File(fileName);
		FileInputStream fis;

		try {
			fis = new FileInputStream(file);
			ObjectInputStream ois;

			try {
				ois = new ObjectInputStream(fis);
				DBInputOutput tmp;
				try {

					tmp = (DBInputOutput) ois.readObject();
					this.returnFields(tmp);
				
				} catch (ClassNotFoundException e) {
					System.out.println("Class not found!");
					e.printStackTrace();
					return false;
					
				}
			} catch (IOException e) {
				System.out.println("Not able to serialize");
				e.printStackTrace();
				return false;
			} finally {
				try {
					fis.close();
				} catch (IOException e) {
					System.out.println("Can't close the file!");
					e.printStackTrace();
					return false;
				}				
			}
		} catch (FileNotFoundException e) {
			System.out.println("Not able to write to file: " + fileName);
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	public Boolean loadXML(String fileName) {
		byte[] encoded;

		try {
			encoded = Files.readAllBytes(Paths.get(fileName));
			XStream xstream = new XStream(new DomDriver());

			// no supress :-)
			DBInputOutput tmp = (DBInputOutput) xstream.fromXML(new String(encoded, "UTF-8"));

			this.returnFields(tmp);

		} catch (IOException e) {
			System.out.println("ERROR: XML Load failed!");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Will fill fields so they can be saved
	 */
	private void populateFields() {
		DB db = DB.obj();

		this.genres = db.getGenres();
		this.genresDirty = db.getGenresDirty();
		this.movies = db.getMovies();
		this.users = db.getUsers();
	}

	/**
	 * After load will return fields to main DB class where they are really
	 * accessed and used.
	 */
	private void returnFields(DBInputOutput loaded) {
		DB db = DB.obj();
		db.setGenres(loaded.genres);
		db.setGenresDirty(loaded.genresDirty);
		db.setMovies(loaded.movies);
		db.setUsers(loaded.users);
	}

	public Boolean saveDAT(String fileName) {
		this.populateFields();

		File file = new File(fileName);
		FileOutputStream fos;

		try {
			fos = new FileOutputStream(file);
			ObjectOutputStream oos;

			try {
				oos = new ObjectOutputStream(fos);
				oos.writeObject(this);

			} catch (IOException e) {
				System.out.println("Not able to serialize");
				e.printStackTrace();
			}
			
			try {
				fos.close();
			} catch (IOException e) {
				System.out.println("Can't close the file!");
				e.printStackTrace();
				return false;				
			}

		} catch (FileNotFoundException e) {
			System.out.println("Not able to write to file: " + fileName);
			e.printStackTrace();
			return false;
		} 
		return true;
	}

	public Boolean saveXML(String fileName) {
		this.populateFields();

		XStream xstream = new XStream(new DomDriver());

		PrintWriter out;

		try {
			out = new PrintWriter(fileName);
			out.println(xstream.toXML(this));
			out.close();
		} catch (FileNotFoundException e) {
			System.out.println("Not able to write to file: " + fileName);
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Internal convertio from String to Int, so the try, catch will not confuse
	 * code elsewhere
	 * 
	 * @param text
	 * @return
	 */
	private int toInt(String text) {
		try {
			return Integer.parseInt(text);
		} catch (NumberFormatException e) {
			if (VERBOSE) System.out.println("Wrong numbers, but we can continue: " + text);
			return -1;
		}

	}
}
