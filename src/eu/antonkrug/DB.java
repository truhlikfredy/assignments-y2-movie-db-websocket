package eu.antonkrug;

import java.util.ArrayList;

public class DB {

	public ArrayList<Movie>					movies;
	public ArrayList<MovieCategory>	categories;

	private static DB								instance	= null;

	public DB() {
		this.movies = new ArrayList<Movie>();
		this.categories = new ArrayList<MovieCategory>();
	}

	/**
	 * Create new or return previous instance of this object, so each class can
	 * access the same common object.
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
	 * @return
	 */
	public static DB obj() {
		return getInstance();
	}
	

}
