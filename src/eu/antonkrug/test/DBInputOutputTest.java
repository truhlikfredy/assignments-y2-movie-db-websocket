package eu.antonkrug.test;

import static org.junit.Assert.*;

import org.junit.Test;

import eu.antonkrug.DB;
import eu.antonkrug.DBInputOutput;
import eu.antonkrug.DBInputOutputEnum;

public class DBInputOutputTest {
	
	private void checkDB() {
		DB db = DB.getInstance();

		// check size
		assertEquals(55, db.getMovies().size());
		assertEquals(87, db.getUsers().size());

		// movie
		assertEquals("The Limey", db.getMovies().get(10).getName());
		assertEquals((Integer) 1999, db.getMovies().get(10).getYear());
		assertEquals("Crime", db.getMovies().get(10).getCategory().getName());

		// check user cust5
		assertEquals("Cust5", db.getUsers().get(10).getUserName());
		assertEquals("Henry", db.getUsers().get(10).getFirstName());
		assertEquals("Shefflin", db.getUsers().get(10).getLastName());

		assertEquals(31, db.getUsers().get(10).getRatingMovie().get(2));

		assertEquals(-3, db.getUsers().get(10).getRatingRating().get(0));
		assertEquals(3, db.getUsers().get(10).getRatingRating().get(1));
		assertEquals(5, db.getUsers().get(10).getRatingRating().get(2));

	}

	private void checkDBPassowrds() {
		DB db = DB.getInstance();
		
		// empty passwords are setup to secret when loaded in by CSV and other
		// loading methods should have it already specified what password is 
		assertTrue(db.getUsers().get(10).matchPassword("secret"));		
		
		assertTrue(db.getUsers().get(3).matchPassword("god"));
	}

	@Test
	public void testFileExtension() {
		assertEquals("",DBInputOutput.fileExtension("hello"));
		assertEquals("ext",DBInputOutput.fileExtension("hello.ext"));
		assertEquals("ext",DBInputOutput.fileExtension("hi.dat/haya.ext"));
		assertEquals("dat",DBInputOutput.fileExtension("/hello.dat"));
	}

	@Test
	public void testFileBase() {
		assertEquals("hello",DBInputOutput.fileBase("hello"));
		assertEquals("hello",DBInputOutput.fileBase("hello.ext"));
		assertEquals("hi.dat/haya",DBInputOutput.fileBase("hi.dat/haya.ext"));
		assertEquals("/hello",DBInputOutput.fileBase("/hello.dat"));
	}

	@Test
	public void testLoadCSV() {
		DBInputOutputEnum.getInstance("JUnitTestData/movies.csv").load();
		this.checkDB();
		this.checkDBPassowrds();
	}

	@Test
	public void testLoadDAT() {
		DBInputOutputEnum.getInstance("JUnitTestData/database.dat").load();
		this.checkDB();
		this.checkDBPassowrds();
	}

	@Test
	public void testLoadXML() {
		DBInputOutputEnum.getInstance("JUnitTestData/database.xml").load();
		this.checkDB();
		this.checkDBPassowrds();
	}

	@Test
	public void testSaveDAT() {
		//first convert it into teste format
		DBInputOutputEnum.getInstance("JUnitTestData/movies.csv").load();
		DBInputOutputEnum.getInstance("JUnitTestData/JUnitSave.dat").save();
		
		//empty database and check if it's really empty
		DB.getInstance().purgeDB();
		assertEquals(0,DB.getInstance().getUsers().size());
		
		//then load the converted format and check if everything is as it should
		DBInputOutputEnum.getInstance("JUnitTestData/JUnitSave.dat").load();
		this.checkDB();
		this.checkDBPassowrds();
	}

	@Test
	public void testSaveCSV() {
		//first convert it into teste format
		DBInputOutputEnum.getInstance("JUnitTestData/movies.csv").load();
		DBInputOutputEnum.getInstance("JUnitTestData/JUnitSave.csv").save();
		
		//empty database and check if it's really empty
		DB.getInstance().purgeDB();
		assertEquals(0,DB.getInstance().getUsers().size());
		
		//then load the converted format and check if everything is as it should
		DBInputOutputEnum.getInstance("JUnitTestData/JUnitSave.csv").load();
		this.checkDB();
		
		//can't check passwords because for import they are considered as plain text
		//but for export they are encrypted because it's the way they are storred in
		//memory. there is no way to get plain text passwords back from them
//		this.checkDBPassowrds();
	}

	@Test
	public void testSaveXML() {
		DBInputOutputEnum.getInstance("JUnitTestData/movies.csv").load();
		DBInputOutputEnum.getInstance("JUnitTestData/JUnitSave.xml").save();
		
		//empty database and check if it's really empty
		DB.getInstance().purgeDB();
		assertEquals(0,DB.getInstance().getUsers().size());
		
		//then load the converted format and check if everything is as it should
		DBInputOutputEnum.getInstance("JUnitTestData/JUnitSave.xml").load();
		this.checkDB();
		this.checkDBPassowrds();
	}

}
