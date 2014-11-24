package eu.antonkrug;

/**
 * Main database class, it will contain all data.
 * 
 * @author Anton Krug
 * 
 */

public class DotCacheEntry {
	private int	userIDa;
	private int	userIDb;
	private int	timeStamp;
	private int	sum;

	/**
	 * Contructor for this class
	 * 
	 * @param userIDa
	 * @param userIDb
	 * @param timeStamp
	 * @param sum
	 */
	public DotCacheEntry(int userIDa, int userIDb, int timeStamp, int sum) {
		super();
		this.userIDa = userIDa;
		this.userIDb = userIDb;
		this.timeStamp = timeStamp;
		this.sum = sum;
	}

	/**
	 * @return the userIDa
	 */
	public int getUserIDa() {
		return userIDa;
	}

	/**
	 * @param userIDa
	 *          the userIDa to set
	 */
	public void setUserIDa(int userIDa) {
		this.userIDa = userIDa;
	}

	/**
	 * @return the userIDb
	 */
	public int getUserIDb() {
		return userIDb;
	}

	/**
	 * @param userIDb
	 *          the userIDb to set
	 */
	public void setUserIDb(int userIDb) {
		this.userIDb = userIDb;
	}

	/**
	 * Returns ID of the other user, if A give it will return B. If B give it will
	 * return A.
	 * 
	 * @param UserID
	 * @return
	 */
	public int getOther(int userID) {
		return ( (userID == userIDa) ?userIDb : userIDa);
	}

	/**
	 * @return the timeStamp
	 */
	public int getTimeStamp() {
		return timeStamp;
	}

	/**
	 * @param timeStamp
	 *          the timeStamp to set
	 */
	public void setTimeStamp(int timeStamp) {
		this.timeStamp = timeStamp;
	}

	/**
	 * @return the sum
	 */
	public int getSum() {
		return sum;
	}

	/**
	 * @param sum
	 *          the sum to set
	 */
	public void setSum(int sum) {
		this.sum = sum;
	}

	/**
	 * Will act as UserIDa and UserIDb are interchangable, so order or naming
	 * doesn't matter, UserIDa will match on UserIDb if they are same ID number.
	 * 
	 * This should then force get to behave in some specific way. Get will give
	 * same result get(10,2); as get(2,10);
	 * 
	 * This can be used to just fill half of the array and save memorry. Because
	 * compatibility between object A and object B will be same as between B and A
	 * and even will be just stored in 1 memorry location not 2. So therefor 50%
	 * memory saving.
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		DotCacheEntry other = (DotCacheEntry) obj;

		if ((userIDa == other.userIDa) || (userIDb == other.userIDb)) return true;
		if ((userIDa == other.userIDb) || (userIDb == other.userIDa)) return true;
		return false;
	}

}
