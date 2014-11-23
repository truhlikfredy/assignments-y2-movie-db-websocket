package eu.antonkrug;

public enum Rating {
	TERRIBLE((byte)(-5)),
	DIDNT_LIKED((byte)(-3)),
	NOT_SEEN((byte)(0)),
	OK((byte)(1)),
	LIKED((byte)(3)),
	REALLY_LIKED((byte)(5));
	
	private final byte score;
	
	private static final Rating values[] = values();
	
	Rating(byte scoreInit) {
		score=scoreInit;
	}
	
	public byte getScore() {
		return score;
	}
	
	public byte getValue() {
		return score;
	}
	
	public static Rating getRating(byte value) {
		return values[value];
	}

}
