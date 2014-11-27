package eu.antonkrug;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;

// Benchmark: http://cern.antonkrug.eu/

// low memory footprint and high performance libaries http://trove.starlight-systems.com/
//import gnu.trove.list.array.TByteArrayList;
//import gnu.trove.list.array.TIntArrayList;


import org.apache.commons.collections4.Predicate;

//high performance scientific libaries https://dst.lbl.gov/ACSSoftware/colt/
import cern.colt.list.IntArrayList;
import cern.colt.list.ByteArrayList;

/**
 * User class, contains sha-1 hashing mechanism for better password security.
 * Even if database will be breached user passwords will not be in plain text.
 * 
 * @author Anton Krug
 * 
 */

public class User implements Comparable<User>, Serializable {

	/**
	 * Generate new ID if you will change anything in this class, if you change
	 * stuff often then comment it out and let the compiler generate one for you
	 * which will change automaticly if you will do any modifications to the class
	 */
	private static final long	serialVersionUID	= 5878250555642530179L;

	private static final int	CACHE_ENTRIES			= 100;

	private String						firstName;
	private String						lastName;
	private String						password;
	private int								ratingDirty;
	private boolean 					loggedIn;

	// sparse matrix using primitive types as vectors and minimal object overhead

	// private TIntArrayList ratingMovie;
	// private TByteArrayList ratingRating;
	private IntArrayList			ratingMovie;
	private ByteArrayList			ratingRating;
	private String						userName;
	private ArrayList<Cache>	topCache;
	private ArrayList<Movie>  reccomended;

	// Dirty is not Boolean but int, so I could support different algorithms,
	// merge sort for little bit dirty arrays and quick sort for very dirty
	// arrays.

	/**
	 * Constructor for User class
	 * 
	 * @param userName
	 * @param firstName
	 * @param lastName
	 * @param password
	 */
	public User(String userName, String firstName, String lastName, String password) {
		this.userName = userName;
		this.firstName = firstName;
		this.lastName = lastName;
		this.setPassword(password);
		// this.ratingMovie = new TIntArrayList();
		// this.ratingRating = new TByteArrayList();
		this.ratingMovie = new IntArrayList();
		this.ratingRating = new ByteArrayList();
		this.ratingDirty = 0;
	}

	/**
	 * @return the reccomended
	 */
	public ArrayList<Movie> getReccomended() {
		return reccomended;
	}

	/**
	 * @param reccomended the reccomended to set
	 */
	public void setReccomended(ArrayList<Movie> reccomended) {
		this.reccomended = reccomended;
	}

	/**
	 * @return the ratingDirty
	 */
	public int getRatingDirty() {
		return ratingDirty;
	}

	/**
	 * @param ratingDirty the ratingDirty to set
	 */
	public void setRatingDirty(int ratingDirty) {
		this.ratingDirty = ratingDirty;
	}

	/**
	 * @return the loggedIn
	 */
	public boolean getLoggedIn() {
		return loggedIn;
	}

	/**
	 * @param loggedIn the loggedIn to set
	 */
	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}

	/**
	 * Add new score rating to this users database
	 * 
	 * @param movie
	 * @param rating
	 */
	public void addRating(int movie, byte rating) {
		this.ratingMovie.add(movie);
		this.ratingRating.add(rating);
		
		DB.obj().getMovie(movie).addRating(rating);

		this.ratingDirty++;
	}

	/**
	 * Empties cache
	 */
	public void purgeCache() {
		this.topCache = new ArrayList<Cache>();
	}

	/**
	 * Calculate compatibility for all and keep just few top
	 */

	public void calculateAll() {
		this.purgeCache();
		ArrayList<Cache> tmp = new ArrayList<Cache>();

		int bucket=0;
		for (User user : DB.obj().getUsers()) {
			int scoreSum = this.calculateCompability(user);
			
			//if there is enough good matches stop
			if (scoreSum>Cache.CACHE_THRESHOLD) bucket+=scoreSum;
			if (bucket>Cache.CACHE_BUCKET) break;
			
			tmp.add(new Cache(user, scoreSum));
		}
		
		Collections.sort(tmp, Cache.BY_SUM_DESC);

		for (int i = 0; i < CACHE_ENTRIES && i < tmp.size(); i++) {
			this.topCache.add(tmp.get(i));
		}
	}

	//TODO adjust score by the size
	/**
	 * Calculate compability for given user
	 * 
	 * @param againtToWhoom
	 * @return
	 */
	public int calculateCompability(User againtToWhoom) {
		if (this.equals(againtToWhoom)) return 0;

		// will return quickly if sort is not necesary
		sortRating();
		int[] leftMovies = this.ratingMovie.elements();
		byte[] leftScores = this.ratingRating.elements();

		// will return quickly if sort is not necesary
		againtToWhoom.sortRating();
		int[] rightMovies = againtToWhoom.getRatingMovie().elements();
		byte[] rightScores = againtToWhoom.getRatingRating().elements();

		int right = 0;
		int sum = 0;

		// go for each rating of both users and find matching ones, on matching ones
		// calculated compatibility score
		for (int left = 0; left < leftMovies.length; left++) {
			// progress right pointer till we are on same or futher movie;
			while (right < rightMovies.length - 1 && leftMovies[left] > rightMovies[right])
				right++;

			// if they both rated the same movie
			if (leftMovies[left] == rightMovies[right]) {
				sum += leftScores[left] * rightScores[right];
			}
		}
		
		return sum;
		
	}

	// *********** GETTERs ********************
	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	/**
	 * Gettin just hash of password is not so risky as getting plain text
	 * password.
	 * 
	 * @return
	 */
	public String getPassword() {
		return password;
	}

	public byte getRating(int movie) {
		// if it's dirty sort it first so we can use binary search
		// will return quickly if sort is not necesary
		sortRating();

		return this.ratingRating.getQuick(this.ratingMovie.binarySearch(movie));
	}

	public IntArrayList getRatingMovie() {
		return ratingMovie;
	}

	public ByteArrayList getRatingRating() {
		return ratingRating;
	}

	public String getUserName() {
		return userName;
	}

	/**
	 * Check if the given password matches with the password stored in database
	 * 
	 * @param passString
	 *          plain text version of the password
	 * @return
	 */
	public boolean matchPassword(String passString) {
		return this.password.equals(this.toHash(passString));
	}

	/**
	 * Check if the given password matches with the password stored in database
	 * 
	 * @param passString
	 *          SHA-1 version of the password
	 * @return
	 */
	public boolean matchPasswordHash(String passHash) {
		return this.password.equals(passHash);
	}

	// *********** SETTERs ********************
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setPassword(String password) {
		this.password = this.toHash(password);
	}

	public void setRatingMovie(IntArrayList ratingMovie) {
		this.ratingMovie = ratingMovie;
	}

	public void setRatingRating(ByteArrayList ratingRating) {
		this.ratingRating = ratingRating;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * Will call aproperiate sorting algorithm
	 */
	public void sortRating() {
		if (ratingDirty > 0) {
			// add merge sort and depending how dirty it is change algorithms
			this.sortRatingQuickSort(0, this.ratingMovie.size());
			this.ratingDirty = 0;
		}
	}

	/**
	 * Quick sort implementation for ratings
	 * 
	 * @param left
	 * @param right
	 */
	private void sortRatingQuickSort(int left, int right) {
		int index = left;
		int j = right;
		int pivot = this.ratingMovie.get((left + right) / 2);

		while (index <= j) {
			while (this.ratingMovie.get(index) < pivot)
				index++;
			while (this.ratingMovie.get(j) > pivot)
				j--;

			if (index <= j) this.swapRating(index++, j--);
		}

		if (left < index - 1) sortRatingQuickSort(left, index - 1);
		if (right > index) sortRatingQuickSort(index, right);
	}

	/**
	 * Swap both movie and rating, this way sorting will keep both of them aligned
	 * and can be used by different sorting algorithms to perform swap
	 * 
	 * @param a
	 * @param b
	 */

	private void swapRating(int a, int b) {
		int tmp = this.ratingMovie.get(a);
		this.ratingMovie.set(a, this.ratingMovie.get(b));
		this.ratingMovie.set(b, tmp);

		byte tmpb = this.ratingRating.get(a);
		this.ratingRating.set(a, this.ratingRating.get(b));
		this.ratingRating.set(b, tmpb);
	}

	/**
	 * Will convert string into SHA-1 hex hash
	 * 
	 * @param text
	 * @return
	 */
	public String toHash(String text) {
		MessageDigest cryptHash;
		String ret = "";
		try {
			cryptHash = MessageDigest.getInstance("SHA-1");
			cryptHash.reset();

			try {

				cryptHash.update(text.getBytes("UTF-8"));
				Formatter format = new Formatter();

				for (byte character : cryptHash.digest())
					format.format("%02x", character);

				ret = format.toString();
				format.close();

			} catch (UnsupportedEncodingException e) {
				System.out.println("UTF-8 not supported.");
				e.printStackTrace();
			}

		} catch (NoSuchAlgorithmException e) {
			System.out.println("SHA-1 not found.");
			e.printStackTrace();
		}

		return ret;
	}

	@Override
	public String toString() {
		return "User [userName=" + userName + ", firstName=" + firstName + ", lastName=" + lastName
				+ "]";
	}

	@Override
	public int compareTo(User user) {
		// no other fields do not have to be compared, because userName should be
		// unique and not 2 users with same username should be allowed
		return this.userName.compareTo(user.userName);
	}

}
