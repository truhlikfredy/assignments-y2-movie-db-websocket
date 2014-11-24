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

	private static final long	serialVersionUID	= 1215105944352599392L;
	
	public static final Comparator<Cache>	BY_SUM			= new BySum();
	public static final Comparator<Cache>	BY_SUM_DESC	= new BySumDesc();
	public static final Comparator<Cache>	BY_USER			= new ByUser();

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

	private Boolean	dirty;
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
	public Boolean getDirty() {
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
	public void setDirty(Boolean dirty) {
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
