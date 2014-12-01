package eu.antonkrug.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import eu.antonkrug.DB;
import eu.antonkrug.DBInputOutputEnum;
import eu.antonkrug.User;

public class SearchCachesReccomendation {

	private DBInputOutputEnum	data;
	private DB								db;
	private User							user;

	@Before
	public void setUp() throws Exception {
		data = DBInputOutputEnum.getInstance("JUnitTestData/database.dat");
		data.load();
		this.db = DB.getInstance();
		this.user = db.getUsers().get(1);
	}

	@Test
	public void testRecomended() {
		// this will test sorting and caches as well

		// how many recomendings
		assertEquals(35, user.reccomendations().size());

		// purge precalculated data
		db.purgeCacheForEachUser();
		db.compatibilityForEachUser();
		user.calculateAll();

		// check if get same result
		assertEquals(35, user.reccomendations().size());

		// remove this users ratings
		user.removeAllRatings();

		// can't match your trend with anybody so is not recomending anything
		assertEquals(0, user.reccomendations().size());
	}

	@Test
	public void testSearch() {
		// case sensitivity
		assertEquals(1, db.searchMovie("baTman", user).size());

		// invalid query
		assertEquals(0, db.searchMovie("bewqewq EWQEWQEW", user).size());

		// single movie
		assertEquals(1, db.searchMovie("grape", user).size());

		// single movie & category (bigraphy)
		assertEquals(3, db.searchMovie("grap", user).size());

		// single category
		assertEquals(2, db.searchMovie("graph", user).size());
	}

}
