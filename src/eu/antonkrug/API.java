package eu.antonkrug;

/**
 * This file is auto-generated by baking tools inside the ./bake folder. Do not
 * change directly! To alter values or add new enums, change ./bake/api.py and
 * then run ./bake/pre_make.sh This will ensure that all values are constant
 * between Java, Python and JavaScript and will resolve to same numeric values.
 * 
 * @author Anton Krug
 */

public enum API {
  //@ff  to turn off formating

  NOT_VALID(0),
  R_LOGIN(1),
  A_PASS_FAIL(2),
  A_IS_ADMIN(3),
  R_LOGOUT(4),
  A_OK_DIALOG(5),
  R_GET_USER(6),
  A_USER(6),
  R_GET_MOVIE(7),
  A_MOVIE(8),
  R_ADD_USER(9),
  R_ADD_MOVIE(10),
  R_REMOVE_USER(11),
  R_REMOVE_MOVIE(12),
  R_LIST_USERS(13),
  A_LIST_USERS(14),
  R_LIST_MOVIES(15),
  A_LIST_MOVIES(16),
  R_LIST_RATINGS(17),
  A_LIST_RATINGS(18),
  R_RATE(19),
  R_LOAD(20),
  R_SAVE(21),
  R_SHUTDOWN(22),
  R_PURGE_CACHE(23),
  R_CACHE_DIRTY(24),
  R_LIST_GENRES(25),
  R_EDIT_USER(26),
  ;
  //@fo   turns on back formating

  private static final API[]	allEnums	= values();

  /**
   * Helper to get back enum object if we know just a value
   * 
   * @param number
   * @return
   */
  public static API getEnum(int number) {
    for (API api : allEnums) {
      if (api.getValue() == number) return api;
    }
    return API.NOT_VALID;
  }

  public static API getEnum(Object obj) {
  	int number;
  	try {
  	 number = Integer.parseInt(obj.toString());
  	} catch (NumberFormatException e) {
  		//if can't be converted into number return non valid enum
  		return API.NOT_VALID;
  	}
  	return getEnum(number);
  }

  private final int value;

  /**
   * Constructor for this enum
   * 
   * @param value
   */
  API(int value) {
    this.value = value;
  }

  /**
   * Getting the value for enum
   * 
   * @return
   */
  public int getValue() {
    return value;
  }
}
