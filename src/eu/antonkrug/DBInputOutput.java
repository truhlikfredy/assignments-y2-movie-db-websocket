package eu.antonkrug;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * DB Input Output class is focused on loading and saving data from/to files in
 * three different formats: CVS, XML and bytestreams (DAT extension).
 * 
 * CVS is easy to break, plus lost passwords, because we are keeping them
 * encrypted so we can save them only encrypted, but load is made more for plain
 * excel export so it's expecting unecrypted passwords, maybe use XML instead to
 * export toother applications, but it can get big. Or use bytestream save/load
 * or JSON API. JSON is supported but not for files, just for the API, still you
 * can do JSON requests to get or set all data you need in JSON format through
 * the API.
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

	public final static boolean			VERBOSE						= true;

	public ArrayList<MovieGenre>		genres;
	public boolean									genresDirty;
	public HashMap<Integer, Movie>	movies;
	public ArrayList<User>					users;
	
	/**
	 * Constructor inicialising fields
	 */
	public DBInputOutput() {
		this.movies = new HashMap<Integer, Movie>();
		this.genres = new ArrayList<MovieGenre>();
		this.genresDirty = false;
		this.users = new ArrayList<User>();	
	}

	/**
	 * Get file extension from file name.
	 * 
	 * @param fileName
	 * @return
	 */
	public static String fileExtension(String fileName) {
		String extension = "";

		int i = fileName.lastIndexOf('.');
		int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));

		if (i > p) {
			extension = fileName.substring(i + 1);
		}
		return extension;
	}

	/**
	 * Gets the base filename without extension from the file name
	 * 
	 * @param fileName
	 * @return
	 */
	public static String fileBase(String fileName) {
		String base = "";

		int i = fileName.lastIndexOf('.');
		int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));

		if (i > p) {
			base = fileName.substring(0, i);
		} else {
			base = fileName;
		}

		return base;
	}

	/**
	 * Loading bundle will detect filenames for CSV and call calls on both of
	 * them.
	 * 
	 * @param fileName
	 */
	public boolean loadCSV(String fileName) {

		// detect base and extension of filename
		String extension = DBInputOutput.fileExtension(fileName);
		String base = DBInputOutput.fileBase(fileName);

		if (extension.toLowerCase().equals("csv")) {

			DB.obj().purgeDB();
			
			// load both CVS's
			if (!this.loadCSVfile(base + "." + extension)) return false;
			if (!this.loadCSVfile(base + "-users." + extension)) return false;

			DB.obj().setLoaded(true);
			DB.obj().setLoadedFileName(fileName);

		} else {
			if (VERBOSE) System.out.println("Not CSV extension. Not loading the file");
			return false;
		}
		return true;
	}

	/**
	 * Loading one specific CVS file. Will detect by content which one it is.
	 * 
	 * @param fileName
	 * @return
	 */
	private boolean loadCSVfile(String fileName) {

		BufferedReader br = null;
		String line = "";

		try {

			br = new BufferedReader(new FileReader(fileName));

			boolean userLine = true;
			User lastUser = null;
			boolean detectFile = true;
			boolean movies = false;

			while ((line = br.readLine()) != null) {
				String[] data = line.split(",");

				if (VERBOSE && data.length == 0)
					System.out.println("Wrong line, but still we can continue parsing: " + line);

				// detect what type of file we are opening by the number of cols
				if (detectFile) {
					// find how many commas are inside the line
					int count = line.length() - line.replace(",", "").length();

					if (count > 3)
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

			if (!movies) {
				lastUser = DB.obj().addUser("admin", "Anton", "Krug", "admin");
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

	/**
	 * Load bytestream data
	 * 
	 * @param fileName
	 * @return
	 */
	public boolean loadDAT(String fileName) {
		File file = new File(fileName);
		FileInputStream fis;

		try {
			fis = new FileInputStream(file);
			ObjectInputStream ois;

			try {
				ois = new ObjectInputStream(fis);
				DBInputOutput tmp;
				try {

					// actual reading
					tmp = (DBInputOutput) ois.readObject();
					this.returnFields(tmp);

					DB.obj().setLoaded(true);
					DB.obj().setLoadedFileName(fileName);

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

	/**
	 * Load XML file
	 * 
	 * @param fileName
	 * @return
	 */
	public boolean loadXML(String fileName) {
		byte[] encoded;

		try {
			encoded = Files.readAllBytes(Paths.get(fileName));
			XStream xstream = new XStream(new DomDriver());

			// no supress :-)
			DBInputOutput tmp = (DBInputOutput) xstream.fromXML(new String(encoded, "UTF-8"));

			this.returnFields(tmp);

			DB.obj().setLoaded(true);
			DB.obj().setLoadedFileName(fileName);

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

		for (User user : loaded.users) {
			user.setLoggedIn(false);
		}

		DB db = DB.obj();
		db.setGenres(loaded.genres);
		db.setGenresDirty(loaded.genresDirty);
		db.setMovies(loaded.movies);
		db.setUsers(loaded.users);
	}

	/**
	 * Save bytestream data
	 * 
	 * @param fileName
	 * @return
	 */
	public boolean saveDAT(String fileName) {
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
		DB.obj().setLoadedFileName(fileName);

		return true;
	}

	public boolean saveCSV(String fileName) {
		String base = DBInputOutput.fileBase(fileName);
		String ext = DBInputOutput.fileExtension(fileName);

		// save both files at once
		if (!saveCSVMovies(fileName)) return false;
		if (!saveCSVUsers(base + "-users." + ext)) return false;

		DB.obj().setLoadedFileName(fileName);

		return true;
	}

	/**
	 * Saving CSV movies
	 * 
	 * @param fileName
	 * @return
	 */
	public boolean saveCSVMovies(String fileName) {
		try {
			File file = new File(fileName);
			if (file.exists()) file.delete();		
			
			FileWriter writer = new FileWriter(fileName);

			for (Map.Entry<Integer, Movie> entry : DB.obj().getMovies().entrySet()) {
				Integer key = entry.getKey();
				Movie movie = entry.getValue();

				writer.append(key.toString());
				writer.append(',');
				writer.append(movie.getName());
				writer.append(',');
				writer.append(movie.getYear().toString());
				writer.append(',');

				String genre = movie.getCategory().getName();
				if (genre.equals("Not categorized")) genre = "";
				writer.append(genre);

				// writer.append("\r\n");
				writer.append("\n");
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			System.out.println("Can't write to file: " + fileName);
			e.printStackTrace();
			return false;
		}

		if (VERBOSE) System.out.println("Saved movies to: " + fileName);
		return true;
	}

	public boolean saveCSVUsers(String fileName) {
		try {
			File file = new File(fileName);
			if (file.exists()) file.delete();		
			
			FileWriter writer = new FileWriter(fileName);

			for (User user : DB.obj().getUsers()) {
				writer.append(user.getUserName());
				writer.append(',');
				writer.append(user.getFirstName());
				writer.append(',');
				writer.append(user.getLastName());
				writer.append(',');
				writer.append(user.getPassword());
				writer.append(",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,");
				writer.append("\n");

				int movieLast = 0;
				boolean first = true;
				for (int i = 0; i < user.getRatingMovie().size(); i++) {
					int movie = user.getRatingMovie().get(i);

					// just fill space between the ratings
					for (int j = movieLast; j < (movie - 1); j++) {
						if (!first) writer.append(',');
						writer.append('0');
						first = false;
					}

					if (!first) writer.append(',');
					writer.append(((Integer) (int) (user.getRatingRating().get(i))).toString());

					movieLast = movie;
					first = false;
				}
				writer.append("\n");

			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			System.out.println("Can't write to file: " + fileName);
			e.printStackTrace();
			return false;
		}

		if (VERBOSE) System.out.println("Saved user to: " + fileName);

		return true;
	}

	/**
	 * Save to XML file
	 * 
	 * @param fileName
	 * @return
	 */
	public boolean saveXML(String fileName) {
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
		DB.obj().setLoadedFileName(fileName);

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
