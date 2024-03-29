package eu.antonkrug;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;

/**
 * Main database class, it will contain all data.
 * 
 * @author Anton Krug
 * 
 */

public class DB implements Serializable {

	private static int				DIRTINES_THRESHOLD	= 10;

	private static DB					instance						= null;

	/**
	 * Generate new ID if you will change anything in this class, if you change
	 * stuff often then comment it out and let the compiler generate one for you
	 * which will change automaticly if you will do any modifications to the class
	 */
	private static final long	serialVersionUID		= 6931673461215366115L;

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
	private int											rattingDirtiness;
	private ArrayList<User>					users;

	/**
	 * Contructor inicialising empty collections
	 */
	public DB() {
		this.loaded = false;
		this.loadedFileName = "";
		this.purgeDB();
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

		movies.put(key, new Movie(key, name, year, genre));
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
	 * @return the loadedFile
	 */
	public String getLoadedFileName() {
		return loadedFileName;
	}

	public Movie getMovie(int index) {
		return this.movies.get(index);
	}

	public HashMap<Integer, Movie> getMovies() {
		return movies;
	}

	/**
	 * return Movies with user ratings populated
	 * 
	 * @param user
	 * @param onlyRated
	 * @return
	 */
	public List<Movie> getMoviesRated(User user, boolean onlyRated) {

		List<Movie> tmp;

		// populate movie list by the rules
		if (onlyRated) {
			tmp = new ArrayList<Movie>();

			// return only movies I rated
			for (int i = 0; i < user.getRatingMovie().size(); i++) {
				int index = user.getRatingMovie().get(i);
				byte rating = Rating.toZeroToFive(user.getRatingRating().get(i));

				movies.get(index).setRated(rating);
				tmp.add(movies.get(index));
			}
		} else {
			// return all movies regardles if user rated them or not

			this.userRatingsFlush(movies);
			this.userRatingsPopulate(movies, user);

			tmp = new ArrayList<Movie>(movies.values());
		}

		// sort list
		Collections.sort(tmp, Movie.BY_RATING);
		return tmp;
	}

	/**
	 * @return the rattingDirtiness
	 */
	public int getRattingDirtiness() {
		return rattingDirtiness;
	}

	public ArrayList<User> getUsers() {
		return users;
	}

	/**
	 * @return the loaded
	 */
	public boolean isLoaded() {
		return loaded;
	}

	public void increaseDirtiness() {
		this.rattingDirtiness++;
	}

	public boolean isTooDirty() {
		if (rattingDirtiness > DIRTINES_THRESHOLD)
			return true;
		else return false;
	}

	/**
	 * Calls IMDB meta getter for each movie
	 */
	public void populateIMDBmetaForEach() {
		for (Entry<Integer, Movie> entry : movies.entrySet()) {
			// System.out.println("Getting info for "+entry.getValue().getName());
			entry.getValue().populateIMDBmeta();
			;
		}
	}

	public void purgeCacheForEachUser() {
		for (User user : this.users) {
			user.purgeCache();
		}
	}

	public void purgeDB() {
		this.movies = new HashMap<Integer, Movie>();
		this.genres = new ArrayList<MovieGenre>();
		this.genresDirty = false;
		this.users = new ArrayList<User>();
		// as default db is considered dirty
		this.rattingDirtiness = DIRTINES_THRESHOLD + 1;
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
			System.out.println("Found and not logged in");
			if (user.matchPassword(pass)) {
				System.out.println("Password match");
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

	/**
	 * Returns index of category which contains query keyword (case insensitive)
	 * 
	 * @param query
	 * @return
	 */
	public int searchGenreContains(String query) {
		for (int i = 0; i < genres.size(); i++) {
			if (genres.get(i).getName().toLowerCase().contains(query.toLowerCase())) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Case insensitive search movies/genre containing query string
	 * 
	 * @param query
	 * @return
	 */
	public List<Movie> searchMovie(String query, User user) {
		HashMap<Integer, Movie> tmp = new HashMap<Integer, Movie>();

		query = query.toLowerCase();
		MovieGenre category = null;

		// find if it's matching a genre
		int genre = searchGenreContains(query);
		// System.out.println(genre);
		if (genre != -1) {
			category = genres.get(genre);
		}

		// compare categories & genres
		for (Entry<Integer, Movie> entry : movies.entrySet()) {
			Movie movie = entry.getValue();
			if (movie.getName().toLowerCase().contains(query) || movie.getCategory().equals(category)) {
				tmp.put(entry.getKey(), movie);
			}
		}
		// System.out.println(tmp.size());

		this.userRatingsPopulate(tmp, user);

		List<Movie> ret = new ArrayList<Movie>(tmp.values());
		// System.out.println(ret.size());
		Collections.sort(ret, Movie.BY_RATING);

		return ret;
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

	/**
	 * @param loadedFile
	 *          the loadedFile to set
	 */
	public void setLoadedFileName(String loadedFileName) {
		this.loadedFileName = loadedFileName;
	}

	public void setMovies(HashMap<Integer, Movie> movies) {
		this.movies = movies;
	}

	/**
	 * @param rattingDirtiness
	 *          the rattingDirtiness to set
	 */
	public void setRattingDirtiness(int rattingDirtiness) {
		this.rattingDirtiness = rattingDirtiness;
	}

	public void setUsers(ArrayList<User> users) {
		this.users = users;
	}

	/**
	 * Clear the user ratings for movies list
	 */
	public void userRatingsFlush(HashMap<Integer, Movie> list) {
		for (Entry<Integer, Movie> entry : list.entrySet()) {
			entry.getValue().setRated((byte) 0);
		}
	}

	/**
	 * Populate user ratings in the list
	 */
	public HashMap<Integer, Movie> userRatingsPopulate(HashMap<Integer, Movie> list, User user) {
		for (int i = 0; i < user.getRatingMovie().size(); i++) {
			int index = user.getRatingMovie().get(i);
			byte rating = Rating.toZeroToFive(user.getRatingRating().get(i));

			if (list.containsKey(index)) list.get(index).setRated(rating);
		}
		return list;
	}

}
