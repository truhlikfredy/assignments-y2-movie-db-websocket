package eu.antonkrug;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.text.SimpleDateFormat;
import java.util.Date;

import mws.WebSocket;
import mws.handshake.ClientHandshake;
import mws.server.WebSocketServer;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

//@ff
/**
 * Main Class which is running server instance of the application
 * 
 * @author Anton Krug
 * 
 * 
 * API request examples:
 * {"t":1,"name":"Cust1","pass":"god"}    //log in
 * {"t":4}  															//LOG OUT
 * {"t":1,"name":"admin","pass":"admin"}  //log as admin
 * {"t":17}														   	//see ratings
 * {"t":25}															  //see genres
 * {"t":15}															  //see movies
 * 
 */
//@fo
public class Server extends WebSocketServer {

	public final static boolean	VERBOSE			= true;
	public final static String	DEFAULT_DB	= "data/database.dat";

	private DB									db;

	/**
	 * Outputs logging message on the console, will print out web socket
	 * connection and time as well.
	 * 
	 * @param ws
	 * @param txt
	 */
	private void log(WebSocket ws, String txt) {
		System.out.println(new SimpleDateFormat("HH:mm:ss").format(new Date()) + " : "
				+ ws.getRemoteSocketAddress() + " " + txt);
	}

	/**
	 * Checking if the port is open and not used
	 * 
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

	/**
	 * On Shutdown / QUIT do exiting procedure
	 */
	static class Message extends Thread {

		public void run() {
			System.out.println("Exiting...");
			if (DB.obj().isLoaded()) {
				// data was loaded / saved to some file, let's save it to the same file
				// before exiting
				String fileName = DB.obj().getLoadedFileName();

				// do rotation of files
				for (int i = 9; i >= 0; i--) {
					File file = new File(fileName + "-" + i);
					if (file.exists()) {
						File fileTo = new File(fileName + "-" + (i + 1));
						file.renameTo(fileTo);
					}
				}
				// delete last
				File file = new File(fileName + "-10");
				if (file.exists()) file.delete();

				// backup actual file
				file = new File(fileName);
				if (file.exists()) {
					file.renameTo(new File(fileName + "-0"));
				}

				DBInputOutputEnum data = DBInputOutputEnum.getInstance(fileName);
				data.save();
			}
			System.out.println("Bey.");
		}
	}

	/**
	 * The main method to kick off the server
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws InterruptedException {

		Runtime runtime = Runtime.getRuntime();
		runtime.addShutdownHook(new Message());

		// The port number. It's over 9000!! But let's check if it's free first.
		int port = 9001;
		if (Server.available(port)) {

			DBInputOutputEnum data = DBInputOutputEnum.getInstance(Server.DEFAULT_DB);
			data.load();
			
//			System.out.println(JSONValue.toJSONString(DB.obj().getGenres()));
			
/*			System.out.println(DB.obj().getMovies().get(1).toJSONString());
			System.out.println(JSONValue.toJSONString(DB.obj().getMovies()));*/
			
//			JSONObject obj = new JSONObject();
//			obj.put("v",DB.obj().getMovies());
//			obj.put("t", API.A_LIST_GENRES.getValue());
//			System.out.println(obj.toString());
			

//			data = DBInputOutputEnum.getInstance("data/movies.csv");
//			DB.obj().purgeCacheForEachUser();
//			data.convertTo("data/test.xml");
			
			// data = DBInputOutputEnum.getInstance("data/test.csv");
			// data.save();

//			data=DBInputOutputEnum.getInstance("data/test.dat");
//			data.save();

			Server svr = new Server(port);
			svr.start();

			BufferedReader sysin = new BufferedReader(new InputStreamReader(System.in));
			while (true) {
				String in;
				try {
					in = sysin.readLine();
					if (in.equals("exit")) {
						svr.stop();
						break;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		} else {
			System.out.println("ERROR: Port " + port + " taken!");
		}
	}

	/**
	 * Prepare server to start
	 * 
	 * @param port
	 */
	public Server(int port) {
		super(new InetSocketAddress(port));

		db = DB.getInstance();
		System.out.println("WS on port " + port + " listening ... ");
		System.out.println("To shutdown the server type: exit and press return. ");
	}

	/**
	 * When client connects
	 */
	@Override
	public void onOpen(WebSocket ws, ClientHandshake ch) {
		this.log(ws, "connected");
	}

	/**
	 * On client disconect
	 */
	@Override
	public void onClose(WebSocket ws, int i, String string, boolean bln) {
		this.log(ws, "disconnected");
		db.RLogOut(ws.getUserID());
		ws.setLogged(false);
	}

	/**
	 * Respond to all request client sends.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void onMessage(WebSocket ws, String string) {
		this.log(ws, "got " + string);

		JSONObject json = (JSONObject) JSONValue.parse(string);

		API request = API.getEnum(json.get("t"));
		System.out.println(request);

		switch (request) {

		// will ignore answer types and non valid ones
			case A_IS_ADMIN:
			case A_LIST_MOVIES:
			case A_LIST_RATINGS:
			case A_LIST_USERS:
			case A_MOVIE:
			case A_OK_DIALOG:
			case A_PASS_FAIL:
			case A_USER:
			case NOT_VALID:
				if (VERBOSE) System.out.print("Got wrong request type: " + request);
				break;

			case R_ADD_MOVIE:
				break;
			case R_ADD_USER:
				break;

			case R_CACHE_DIRTY:
				break;

			case R_EDIT_USER:
				break;

			case R_GET_MOVIE:
				if (ws.isLogged()) {
					ws.send(JSONValue.toJSONString(db.getMovie(Integer.parseInt(json.get("id").toString()))));
				}
				break;

			case R_GET_USER:
				if (ws.isLogged()) {
					ws.send(JSONValue.toJSONString(db.getUsers().get(ws.getUserID())));
				}
				break;

			case R_LIST_GENRES:
				if (ws.isLogged()) {
					JSONObject obj = new JSONObject();
					obj.put("v",db.getGenres());
					obj.put("t", API.A_LIST_GENRES.getValue());
					System.out.println(obj.toJSONString());
					ws.send(obj.toJSONString());
										
				}
				break;

			case R_LIST_MOVIES:
				if (ws.isLogged()) {
					ws.send(JSONValue.toJSONString(db.getMovies()));
				}
				break;

			case R_LIST_RATINGS:
				if (ws.isLogged()) {
					User user = db.getUsers().get(ws.getUserID());
					ws.send(JSONValue.toJSONString(user.getRatings()));
				}
				break;

			case R_LIST_USERS:
				if (ws.isLogged()) {
					ws.send(JSONValue.toJSONString(db.getUsers()));
				}
				break;

			case R_LOAD:
				if (ws.isAdmin()) {
					DBInputOutputEnum data = DBInputOutputEnum.getInstance(json.get("file").toString());
					data.load();
				}
				break;

			case R_LOGIN:
				if (VERBOSE)
					this.log(ws, "Tries to log in:" + json.get("name") + " password " + json.get("pass"));

				int ret = db.RLogIn(json.get("name").toString(), json.get("pass").toString());

				// System.out.println(ret);

				// if looged in setup some variables
				if (ret != -1) {
					ws.setLogged(true);
					ws.setUserID(ret);
					if (json.get("name").toString().equals("admin")) {
						ws.setAdmin(true);
					}
				}
				ws.send("{\"t\":" + API.A_PASS_FAIL.getValue() + ",\"v\":" + ((ret != -1) ? true : false)
						+ ",\"admin\":" + ws.isAdmin() + "}");
				break;

			case R_LOGOUT:
				db.RLogOut(ws.getUserID());
				ws.setLogged(false);
				break;

			case R_PURGE_CACHE:
				if (ws.isAdmin()) {
					db.purgeCacheForEachUser();
				}
				break;

			case R_RATE:
				break;

			case R_REMOVE_MOVIE:
				if (ws.isAdmin()) {
					db.getMovies().remove(Integer.parseInt(json.get("val").toString()));
				}
				break;

			case R_REMOVE_USER:
				if (ws.isAdmin()) {
					db.getUsers().remove(Integer.parseInt(json.get("val").toString()));
				}
				break;

			case R_SAVE:
				if (ws.isAdmin()) {
					DBInputOutputEnum data = DBInputOutputEnum.getInstance(json.get("name").toString());
					data.save();
				}
				break;

			default:
				break;

		}
	}

	/**
	 * When error happens inside some methods while serving in this thread with
	 * this worker.
	 */
	@Override
	public void onError(WebSocket ws, Exception excptn) {
		this.log(ws, " ERROR! " + excptn.toString());
	}

}
