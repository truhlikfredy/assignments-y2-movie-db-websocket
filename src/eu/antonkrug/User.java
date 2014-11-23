package eu.antonkrug;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

// Benchmark: http://cern.antonkrug.eu/

// low memory footprint and high performance libaries http://trove.starlight-systems.com/
//import gnu.trove.list.array.TByteArrayList;
//import gnu.trove.list.array.TIntArrayList;

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

public class User {
	private String				firstName;
	private String				lastName;
	private String				password;
	private int						ratingDirty;

	// sparse matrix using primitive types as vectors and minimal object overhead

	// private TIntArrayList ratingMovie;
	// private TByteArrayList ratingRating;
	private IntArrayList	ratingMovie;
	private ByteArrayList	ratingRating;
	private String				userName;

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
	 * Add new score rating to this users database
	 * 
	 * @param movie
	 * @param rating
	 */
	public void addRating(int movie, byte rating) {
		this.ratingMovie.add(movie);
		this.ratingRating.add(rating);

		this.ratingDirty++;
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
		// if it's dirty sort it first
		if (ratingDirty > 0) sortRating();

		return this.ratingRating.getQuick(this.ratingMovie.indexOf(movie));
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
	public Boolean matchPassword(String passString) {
		return this.password.equals(this.toHash(passString));
	}

	/**
	 * Check if the given password matches with the password stored in database
	 * 
	 * @param passString
	 *          SHA-1 version of the password
	 * @return
	 */
	public Boolean matchPasswordHash(String passHash) {
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

	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * Will call aproperiate sorting algorithm
	 */
	private void sortRating() {
		// add merge sort and depending how dirty it is change algorithms
		this.sortRatingQuickSort(0, this.ratingMovie.size());
		this.ratingDirty = 0;
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

}
