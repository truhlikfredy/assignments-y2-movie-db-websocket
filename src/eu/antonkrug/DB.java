package eu.antonkrug;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

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
	private Boolean									genresDirty;
	private HashMap<Integer, Movie>	movies;
	private ArrayList<User>					users;

	public DB() {
		this.movies = new HashMap<Integer, Movie>();
		this.genres = new ArrayList<MovieGenre>();
		this.genresDirty = false;
		this.users = new ArrayList<User>();
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

	public User addUser(String userName, String firstName, String lastName, String password) {
		User user = new User(userName, firstName, lastName, password);
		this.users.add(user);
		return user;
	}

}
