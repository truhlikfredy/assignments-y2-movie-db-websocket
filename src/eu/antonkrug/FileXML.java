package eu.antonkrug;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class FileXML implements Serializable {

	private static final long				serialVersionUID	= -923545324660029399L;

	public ArrayList<MovieGenre>		genres;
	public Boolean									genresDirty;
	public HashMap<Integer, Movie>	movies;
	public ArrayList<User>					users;

	/**
	 * Will fill fields so they can be saved
	 */
	private void populateFields() {
		DB db = DB.obj();

		this.genres = db.getGenres();
		this.genresDirty = db.getGenresDirty();
		this.movies = db.getMovies();
		this.users = db.getUsers();
	}

	/**
	 * After load will return fields to main DB class where they are really
	 * accessed and used.
	 */
	private void returnFields(FileXML loaded) {
		DB db = DB.obj();
		db.setGenres(loaded.genres);
		db.setGenresDirty(loaded.genresDirty);
		db.setMovies(loaded.movies);
		db.setUsers(loaded.users);		
	}

	public void saveXML(String fileName) {
		this.populateFields();

		XStream xstream = new XStream(new DomDriver());

		PrintWriter out;

		try {
			out = new PrintWriter(fileName);
			out.println(xstream.toXML(this));
			out.close();
		} catch (FileNotFoundException e) {
			System.out.println("Not able to write to file: " + fileName);
			e.printStackTrace();
		}

	}

	public void saveDat(String fileName) {
		this.populateFields();

		File file = new File(fileName);
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(file);
			ObjectOutputStream oos;
			try {
				oos = new ObjectOutputStream(fos);
				oos.writeObject(this);
			} catch (IOException e) {
				System.out.println("Not able to serialize to file");
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			System.out.println("Not able to write to file: " + fileName);
			e.printStackTrace();
		}

	}

	public void loadXML(String fileName) {
		byte[] encoded;
		try {
			encoded = Files.readAllBytes(Paths.get(fileName));
			XStream xstream = new XStream(new DomDriver());

			// no supress :-)
			FileXML tmp = (FileXML) xstream.fromXML(new String(encoded, "UTF-8"));
			
			this.returnFields(tmp);

		} catch (IOException e) {
			System.out.println("ERROR: XML Load failed!");
			e.printStackTrace();
		}

	}
}
