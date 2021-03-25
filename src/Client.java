import java.io.*;
import java.net.*;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.nio.charset.StandardCharsets;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Client {

	public Socket s = null;
	public BufferedReader input = null;
	public DataOutputStream output = null;
	public ArrayList<ServerObject> servers = null;
	public ServerObject largestServerObject = null;
	public String serverMessage = null;
	public String clientMessage = null; // why is this needed?
	public Boolean receivedNone = null;
	public Client(String localAddress, int port) {

		try {
			s = new Socket(localAddress, port);
			input = new BufferedReader(new InputStreamReader(s.getInputStream()));
			output = new DataOutputStream(s.getOutputStream());
		} catch (UnknownHostException e) {
			System.out.println(e);
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println(e);
			e.printStackTrace();
		}

	}

	// function to read xml file server in the ds-server
	public ArrayList<ServerObject> readXML() {
		ArrayList<ServerObject> serversList = new ArrayList<ServerObject>();
		try {
			File systemXML = new File("pre-compiled/ds-system.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(systemXML);

			doc.getDocumentElement().normalize();
			NodeList servers = doc.getElementsByTagName("server");
			for (int i = 0; i < servers.getLength(); i++) {
				Element server = (Element) servers.item(i);
				String type = server.getAttribute("type");
				int limit = Integer.parseInt(server.getAttribute("limit"));
				int bootUpTime = Integer.parseInt(server.getAttribute("bootupTime"));
				double hourlyRate = Double.parseDouble(server.getAttribute("hourlyRate"));
				int coreCount = Integer.parseInt(server.getAttribute("coreCount"));
				int memory = Integer.parseInt(server.getAttribute("memory"));
				int disk = Integer.parseInt(server.getAttribute("disk"));

				ServerObject serv = new ServerObject(type, limit, bootUpTime, hourlyRate, coreCount, memory, disk);
				// Add all this server we read from xml files to a server class that we created
				serversList.add(serv);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return serversList;
	}

	// method to send to the server
	public void sendToServer(String message) {
		try {
			output.write((message + "\n").getBytes());
			output.flush();
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	public void readFromServer() {
		try {
			//refactor so that this.serverMessage is assigned from input.readLine
			serverMessage = input.readLine();
			receivedNone = serverMessage.equals("NONE");
			System.out.println("server: " + serverMessage + "\n");
		} catch (IOException e) {
			serverMessage = "Error";
			System.out.println(e);
		}
	}

	// main method to do all the client handling

	public void handShake()
	{
		sendToServer("HELO");
		readFromServer(); // OK

		String username = System.getProperty("user.name");
		String authMessage = "AUTH " + username;
		sendToServer(authMessage);
		readFromServer();// OK

		this.receivedNone = false;
	}

	public void quit() throws IOException {
		sendToServer("QUIT");
		readFromServer();
		if(serverMessage.equals("QUIT"))
		{
			input.close(); output.close(); s.close();
		}
	}


	public void start(String[] args) {
		try {
			handShake();
			// Reading XML from server, get largest server, set to client's instance variables
			this.initialiseServer(args);

			sendToServer("REDY");
			readFromServer();


			String[] serverMessageArray = serverMessage.split(" ");

			Job j = null;

			if(serverMessageArray[0].equals("JOBN"))
			{
				j = new Job(serverMessageArray);
				sendToServer("GETS Capable "+j.GET());
			}
			readFromServer(); // DATA n
			int numLines = Integer.parseInt(serverMessage.split(" ")[1]);


			sendToServer("OK");
			ArrayList<String> serverStatuses = readMultiLineFromServer(numLines); // this will contain info of all server statuses //need to have a specific read because of multiline


			sendToServer("OK");
			readFromServer(); // .

			//this is where we should send schedule
			assert serverStatuses != null;
			String serverToScheduleJob = getFirstLargestServerObject(serverStatuses);
			sendToServer("SCHD "+j.jobId+" "+serverToScheduleJob);
			readFromServer();


//			//step 4 is done here.
//			while(!receivedNone)
//			{
//				//step 5
//				sendToServer("REDY");
//
//				//step 6
//				readFromServer(); //might get JOBN, JOBP, ... NONE
//
//
//				String[] serverMessageArray = serverMessage.split(" ");
//				switch (serverMessageArray[0]){
//					case "JOBN":{ //array.length is guaranteed to be 7
//						sendToServer("GETS Capable "+serverMessageArray[4]
//								+" "+serverMessageArray[5] +" "+serverMessageArray[6]);
//					}
//					case "JOBP":{
//
//					}
//
//					case "DATA":{
//						sendToServer("OK");
//					}
//					default:
//						break;
//				}
//			}

			// this line is reached when NONE has been received
			//quit();





//			String[] splitedJob = serverMessage.split("\\s+");
//			for (String i : splitedJob) {
//				System.out.println(i);
//			}
//			Jobs jobs = new Jobs(Integer.parseInt(splitedJob[0]), Integer.parseInt(splitedJob[1]),Integer.parseInt(splitedJob[2]), Integer.parseInt(splitedJob[3]), Integer.parseInt(splitedJob[4]),Integer.parseInt(splitedJob[5]));
			// System.out.println(splited);
			// Receiving Largest Server

			// sendToServer("REDY");

			s.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private ArrayList<String> readMultiLineFromServer(int numLines) {
		try {
			ArrayList<String> lines = new ArrayList<String>();
			for(int i = 0; i < numLines; i++)
			{
				serverMessage = input.readLine();
				lines.add(serverMessage);
				receivedNone = serverMessage.equals("NONE");
			}
			System.out.println(lines.toString());
			return lines;
		} catch (IOException e) {
			serverMessage = "Error";
			System.out.println(e);
			return null;
		}
	}

	public void initialiseServer(String[] args) {
		this.servers = readXML();
		this.largestServerObject = getLargestServer();
		boolean validArg = this.checkArgs(args); // DO SOME ARGUMENT CHECKING
	}

	public ServerObject getLargestServer() {
		ServerObject max = servers.get(0);
		for (ServerObject s : servers) {
			if (max.compareTo(s) < 0) {
				max = s;
			}
		}
		return new ServerObject(max);
	}

	public String getFirstLargestServerObject(ArrayList<String> serverStatuses){
		// assuming the arraylist servers have been initialised
		//serverState is sent as 1 message in page 15 of ds-sim-user-guide

		//note that, while there might be unavailable servers, the client does not have to handle
		// "scheduling to only available/booting.." servers.
		// in later implementations, we could improve on this

		String largestType = getLargestServer().type;
		int id = 0;
		for(String s: serverStatuses)
		{
			String[] splitted = s.split(" ");
			if(splitted[0].equals(largestType))
			{
				id = Math.min(Integer.parseInt(splitted[1]), id);
			}
		}
		return largestType+" "+id; //something like "super-silk 0"
	}

	public boolean checkArgs(String[] argument) {
		boolean flag = false;
		List validArgs = Arrays.asList("bf", "wf", "ff");

		if (argument == null || argument.length != 2) {
			System.out.println("Invalid argument length");
		}

		else if (!argument[0].equals("-a")) {
			System.out.println("Missing argument: -a");
		}

		else if (!validArgs.contains(argument[1]))
		// argument[1] has to be in the list of predefinedAlgo. We do something like
		// predefinedAlgo.contains(argument[1])
		{
			System.out.println("For argument: -a, Invalid choice: " + (argument[1]));
		}

		else {
			System.out.println("Successful client call");
			flag = true;
		}
		return flag;
	}

	public static void main(String[] args) {
		Client client = new Client("127.0.0.1", 50000);
		client.start(args);
		System.out.println("Hello world");
	}
}