package eu.antonkrug;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.text.SimpleDateFormat;
import java.util.Date;

import mws.WebSocket;
import mws.handshake.ClientHandshake;
import mws.server.WebSocketServer;

import org.json.simple.JSONObject;

/**
 * Main Class which is running server instance of the application
 * 
 * @author Anton Krug
 * 
 */
public class Server extends WebSocketServer {

	public final static Boolean	VERBOSE	= true;

	private void log(WebSocket ws, String txt) {
		System.out.println(new SimpleDateFormat("HH:mm:ss").format(new Date()) + " : "
				+ ws.getRemoteSocketAddress() + " " + txt);
	}

	/**
	 * Checking if the port is open and not used
	 * @param port
	 * @return
	 */
	public static boolean available(int port) {
		ServerSocket ss = null;
		DatagramSocket ds = null;
		try {
			ss = new ServerSocket(port);
			ss.setReuseAddress(true);
			ds = new DatagramSocket(port);
			ds.setReuseAddress(true);
			return true;
		} catch (IOException e) {
		} finally {
			if (ds != null) {
				ds.close();
			}

			if (ss != null) {
				try {
					ss.close();
				} catch (IOException e) {
					/* should not be thrown */
				}
			}
		}

		return false;
	}

	public static void main(String[] args) {

		System.out.println(API.getEnum(100));
		System.out.println(API.getEnum(2));
		System.out.println(API.getEnum(1));

		// The port number. It's over 9000!!
		Server svr = new Server(9001);
		svr.start();
	}

	private DB	db;

	public Server(int port) {
		super(new InetSocketAddress(port));

		db = DB.getInstance();
		System.out.println("WS on port " + port + " listening ");
	}

	public void importData() {
		// FileCSV importer = new FileCSV("films_fx.csv","ratings_fx.csv");
		// importer.loadMovies();
		// importer.loadRatings();

		// DBInputOutput xml = new DBInputOutput();
		// xml.saveXML("sasa");
		// xml.loadXML("sasa");
		// xml.loadDat("test.dat");
		// DB.obj().compatibilityForEachUser();
		// DB.obj().purgeCacheForEachUser();
		// xml.saveXML("sasa2");
		// xml.saveDat("test.dat");

		DBInputOutputEnum data = DBInputOutputEnum.getInstance("movies.csv");
		data.load();

		// System.out.println(DB.obj().users.get(0).ratingMovie.get(0));
		// User usr = DB.obj().getUsers().get(0);
		User usr = DB.obj().getUsers().get(0);
		usr.calculateAll();
		System.out.println("Done.");

	}

	@Override
	public void onOpen(WebSocket ws, ClientHandshake ch) {
		this.log(ws, "connected");
		System.out.println("" + ws.getClass());
		System.out.println("" + ws.getUserID());
	}

	@Override
	public void onClose(WebSocket ws, int i, String string, boolean bln) {
		System.out.println("" + ws.getUserID());
		this.log(ws, "disconnected");
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onMessage(WebSocket ws, String string) {
		this.log(ws, "got " + string);

		// because it extends hashmap but it doesn't have type paramter in class
		// definition, if we could do this: JSONObject<String,Object> obj=new
		// JSONObject<String,Object>(); it would fix the warrning but we can't so we
		// have to just supress it
		JSONObject obj = new JSONObject();
		obj.put("name", "foo");
		obj.put("text", string.toUpperCase());
		obj.put("nickname", null);
		ws.setUserID(100);

		ws.send(obj.toString());
	}

	@Override
	public void onError(WebSocket ws, Exception excptn) {
		this.log(ws, " ERROR!");
	}

}
