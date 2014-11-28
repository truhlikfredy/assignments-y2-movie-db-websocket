package eu.antonkrug;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;

/**
 * Main database class, it will contain all data.
 * 
 * @author Anton Krug
 * 
 */

public class DB implements Serializable {

	private static DB					instance					= null;

	/**
	 * Generate new ID if you will change anything in this class, if you change
	 * stuff often then comment it out and let the compiler generate one for you
	 * which will change automaticly if you will do any modifications to the class
	 */
	private static final long	serialVersionUID	= 6931673461215366115L;

	/**
	 * Create new or return previous instance of this object, so each class can
	 * access the same common DB object.
	 * 
	 * @return instance of the common object
	 */
	public static DB getInstance() {
		if (instance == null) {
			instance = new DB();
		}
		return instance;
	}

	/**
	 * Shortcut for getInstance()
	 * 
	 * @return
	 */
	public static DB obj() {
		return getInstance();
	}

	private ArrayList<MovieGenre>		genres;
	private boolean									genresDirty;
	private boolean									loaded;
	private String									loadedFileName;
	private HashMap<Integer, Movie>	movies;
	private ArrayList<User>					users;

	/**
	 * Contructor inicialising empty collections
	 */
	public DB() {
		this.movies = new HashMap<Integer, Movie>();
		this.genres = new ArrayList<MovieGenre>();
		this.genresDirty = false;
		this.users = new ArrayList<User>();
		this.loaded = false;
		this.loadedFileName = "";
	}

	/**
	 * @return the loadedFile
	 */
	public String getLoadedFileName() {
		return loadedFileName;
	}

	/**
	 * @param loadedFile
	 *          the loadedFile to set
	 */
	public void setLoadedFileName(String loadedFileName) {
		this.loadedFileName = loadedFileName;
	}
	
	/**
	 * Add new movie into the collection
	 * 
	 * @param key
	 * @param name
	 * @param year
	 * @param genreName
	 */
	public void addMovie(int key, String name, int year, String genreName) {
		// Find genre already created or create new one

		MovieGenre genre = this.findGenreByName(genreName);

		if (genre == null) {
			genre = new MovieGenre(genreName);
			this.genres.add(genre);
			this.genresDirty = true;
		}
		genre.incTimesUsed();

		movies.put(key, new Movie(name, year, genre));
	}

	/**
	 * Add new user into the collection
	 * 
	 * @param userName
	 * @param firstName
	 * @param lastName
	 * @param password
	 * @return
	 */
	public User addUser(String userName, String firstName, String lastName, String password) {
		User user = new User(userName, firstName, lastName, password);
		this.users.add(user);
		return user;
	}

	/**
	 * Calculate compability of each user
	 */
	public void compatibilityForEachUser() {
		for (User user : this.users) {
			user.calculateAll();
		}
	}

	/**
	 * Binary search on arraylist of genres, if list was modified in mean time, it
	 * will sort itself first.
	 * 
	 * @param name
	 * @return
	 */
	public MovieGenre findGenreByName(String name) {

		/*
		 * If the genres were modified in meantime, sort them first
		 */
		if (this.genresDirty) {
			Collections.sort(this.genres, MovieGenre.BY_NAME);
			this.genresDirty = false;
		}

		int lo = 0;
		int hi = genres.size() - 1;
		while (lo <= hi) {
			// Key is in a[lo..hi] or not present.
			int mid = lo + (hi - lo) / 2;
			if (name.compareTo(genres.get(mid).getName()) < 0)
				hi = mid - 1;
			else if (name.compareTo(genres.get(mid).getName()) > 0)
				lo = mid + 1;
			else return genres.get(mid);
		}
		return null;
	}

	public ArrayList<MovieGenre> getGenres() {
		return genres;
	}

	public boolean getGenresDirty() {
		return genresDirty;
	}

	/**
	 * @return the loaded
	 */
	public boolean isLoaded() {
		return loaded;
	}

	public Movie getMovie(int index) {
		return this.movies.get(index);
	}

	public HashMap<Integer, Movie> getMovies() {
		return movies;
	}

	public ArrayList<User> getUsers() {
		return users;
	}

	public void purgeCacheForEachUser() {
		for (User user : this.users) {
			user.purgeCache();
		}
	}

	public void setGenres(ArrayList<MovieGenre> genres) {
		this.genres = genres;
	}

	public void setGenresDirty(boolean genresDirty) {
		this.genresDirty = genresDirty;
	}

	/**
	 * @param loaded
	 *          the loaded to set
	 */
	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}

	public void setMovies(HashMap<Integer, Movie> movies) {
		this.movies = movies;
	}

	public void setUsers(ArrayList<User> users) {
		this.users = users;
	}

	/**
	 * Request to log in, check if users exists and the password matches
	 * 
	 * @param name
	 * @param pass
	 * @return
	 */
	public int RLogIn(final String name, String pass) {

		User user = CollectionUtils.find(this.users, new Predicate<User>() {
			public boolean evaluate(User arg) {
				return arg.getUserName().equals(name);
			}
		});
		
		if (user != null && !user.getLoggedIn()) {
			if (user.matchPassword(pass)) {
				user.setLoggedIn(true);
				return users.indexOf(user);
			}
		}
		return -1;
	}

	/**
	 * Request to log out user
	 * 
	 * @param userID
	 */
	public void RLogOut(int userID) {
		if (userID != -1) {
			User user = users.get(userID);
			user.setLoggedIn(false);
		}
	}

}
