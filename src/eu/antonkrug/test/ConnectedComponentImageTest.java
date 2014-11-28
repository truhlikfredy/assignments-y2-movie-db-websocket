package eu.antonkrug.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ConnectedComponentImageTest {
	ConnectedComponentImage	img;

	/**
	 * Hardcoded results with shapes2, do not change name here, or content of the
	 * file, or content of JUnitImages folder, unless you know what you are doing
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		img = new ConnectedComponentImage("images/shapes2.bmp");
	}

	@Test
	public void testCountComponents() {
		assertEquals(3, img.countComponents());
	}

	@Test
	public void testIdentifyComonentImage() {
		// had to implement equals for Picture to be able use it this way
		assertEquals(true, img.identifyComonentImage().equals(new Picture("JUnitImages/identify.png")));
	}

	@Test
	public void testColourComponentImage() {
		// had to implement equals for Picture to be able use it this way
		assertEquals(true,
				img.colourComponentImage().equals(new Picture("JUnitImages/colorComponent.png")));
	}

	@Test
	public void testBinaryComponentImage() {
		// had to implement equals for Picture to be able use it this way
		assertEquals(true, img.binaryComponentImage().equals(new Picture("JUnitImages/binary.png")));
	}

}
