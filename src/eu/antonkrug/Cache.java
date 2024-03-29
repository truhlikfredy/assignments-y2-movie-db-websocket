package eu.antonkrug;

import java.io.Serializable;
import java.util.Comparator;

/**
 * One cache entry class
 * 
 * @author Anton Krug
 * 
 */

public class Cache implements Serializable {

	/**
	 * Generate new ID if you will change anything in this class, if you change
	 * stuff often then comment it out and let the compiler generate one for you
	 * which will change automaticly if you will do any modifications to the class
	 */
	private static final long							serialVersionUID	= 1215105944352599392L;

	public static final int								CACHE_THRESHOLD		= 200;
	public static final int								CACHE_BUCKET			= 3000;

	public static final Comparator<Cache>	BY_SUM						= new BySum();
	public static final Comparator<Cache>	BY_SUM_DESC				= new BySumDesc();
	public static final Comparator<Cache>	BY_USER						= new ByUser();

	/**
	 * Caches can be sorted by compatibility sum
	 * 
	 */
	private static class BySum implements Comparator<Cache> {
		public int compare(Cache left, Cache right) {
			return left.sum - right.sum;
		}
	}

	/**
	 * Caches can be sorted by compatibility sum in descending order
	 * 
	 */
	private static class BySumDesc implements Comparator<Cache> {
		// reverse can by used but this way it's done in one step
		public int compare(Cache left, Cache right) {
			return right.sum - left.sum;
		}
	}

	/**
	 * Caches can be sorted by userName of the user
	 * 
	 */
	private static class ByUser implements Comparator<Cache> {
		public int compare(Cache left, Cache right) {
			return left.user.compareTo(right.user);
		}
	}

	private boolean	dirty;
	private int			sum;
	private User		user;

	/**
	 * @param user
	 * @param dirty
	 * @param sum
	 */
	public Cache(User user, int sum) {
		super();
		this.user = user;
		this.dirty = false;
		this.sum = sum;
	}

	/**
	 * @return the dirty
	 */
	public boolean getDirty() {
		return dirty;
	}

	/**
	 * @return the sum
	 */
	public int getSum() {
		return sum;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param dirty
	 *          the dirty to set
	 */
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	/**
	 * @param sum
	 *          the sum to set
	 */
	public void setSum(int sum) {
		this.sum = sum;
	}

	/**
	 * @param user
	 *          the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

}
