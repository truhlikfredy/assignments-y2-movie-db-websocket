package eu.antonkrug;

/**
 * Rating enum which contains all allowed ratings and their score and text
 * values
 * 
 * @author Anton Krug
 * 
 */

public enum Rating {
	//@ff  to turn off formating
	
	TERRIBLE((byte) (-5), "Terrible!"),
	DIDNT_LIKED((byte) (-3), "Didn't like it"),
	NOT_SEEN((byte) (0), "Haven't seent it"),
	OK((byte) (1), "OK"), 
	LIKED((byte) (3), "Liked it!"), 
	REALLY_LIKED((byte) (5), "Really liked it!"),
	;	
	//@fo turns on back formating

	private final byte						score;
	private final String					text;

	private static final Rating[]	allEnums	= values();
	
	public static byte toZeroToFive(byte rating) {
		switch (rating) {
			case (-5): return 1;
			case (-3): return 2;
			case (1): return 3;
			case (3): return 4;
			case (5): return 5;
			
			case (0):
			default:
				return 0;
		}
		
	}

	Rating(byte scoreInit, String textInit) {
		score = scoreInit;
		text = textInit;
	}

	/**
	 * Convert from enum to score value
	 * 
	 * @return
	 */
	public byte getScore() {
		return score;
	}

	/**
	 * Convert from enum to score value
	 * 
	 * @return
	 */
	public byte getValue() {
		return score;
	}

	public String getText() {
		return text;
	}

	/**
	 * Will convert from score to Rating enum
	 * 
	 * @param value
	 * @return
	 */
	public static Rating getRating(byte value) {

		// there is values(ordinal) trick, which is less code, but will not guarante
		// exact maping back as you desire
		/*
		 * switch (value) { case -5: return TERRIBLE;
		 * 
		 * case -3: return DIDNT_LIKED;
		 * 
		 * case 0: return NOT_SEEN;
		 * 
		 * case 1: return OK;
		 * 
		 * case 3: return LIKED;
		 * 
		 * case 5: return REALLY_LIKED;
		 * 
		 * default: System.out.println("ERROR: Getting wrong rating!"); return
		 * NOT_SEEN; }
		 */

		// Or just search for correct one and return it
		for (Rating rating : allEnums) {
			if (rating.getValue() == value) return rating;
		}
		return NOT_SEEN;

	}

	/**
	 * Will convert from score to Rating enum
	 * 
	 * @param value
	 * @return
	 */
	public static Rating getRating(int value) {
		return getRating((byte) (value));
	}

	/**
	 * Will convert from score to Rating enum
	 * 
	 * @param value
	 * @return
	 */
	public static Rating getRating(String value) {
    try {
  		return getRating(Integer.parseInt(value));
    } catch (NumberFormatException e) {
      return getRating(0);
    }
	}

}
