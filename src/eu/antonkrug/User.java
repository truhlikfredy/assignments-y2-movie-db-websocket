package eu.antonkrug;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

// low memory footprint and high performance libaries http://trove.starlight-systems.com/

import gnu.trove.list.array.TByteArrayList;
import gnu.trove.list.array.TIntArrayList;

/**
 * User class, contains sha-1 hashing mechanism for better password security.
 * Even if database will be breached user passwords will not be in plain text.
 * 
 * @author Anton Krug
 * 
 */

public class User {
	private String					userName;
	private String					firstName;
	private String					lastName;
	private String					password;
	// sparse matrix using primitive types as vectors and minimal object overhead
	private TIntArrayList		ratingMovie;
	private TByteArrayList	ratingRating;

	public User(String userName, String firstName, String lastName, String password) {
		this.userName = userName;
		this.firstName = firstName;
		this.lastName = lastName;
		this.setPassword(password);
		this.ratingMovie = new TIntArrayList();
		this.ratingRating = new TByteArrayList();
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void addRating(int movie, byte rating) {
		this.ratingMovie.add(movie);
		this.ratingRating.add(rating);
	}

	public byte getRating(int movie) {
		return this.ratingRating.get(this.ratingMovie.indexOf(movie));
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

	/**
	 * Gettin just hash of password is not so risky as getting plain text
	 * password.
	 * 
	 * @return
	 */
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = this.toHash(password);
	}

	public Boolean matchPassword(String passString) {
		return this.password.equals(this.toHash(passString));
	}

	public Boolean matchPasswordHash(String passHash) {
		return this.password.equals(passHash);
	}

	@Override
	public String toString() {
		return "User [userName=" + userName + ", firstName=" + firstName + ", lastName=" + lastName
				+ "]";
	}

}
