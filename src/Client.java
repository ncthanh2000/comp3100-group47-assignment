import java.io.*;
import java.net.*;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Client {
	public Socket s = null;
	public BufferedReader input = null;
	public DataOutputStream output = null;
	public Map<String, Integer> servertypeCoreDictionary;
	public ArrayList<ServerObject> servers = null;
	public String serverMessage = null;
	public Boolean receivedNone = null;
	public SchedulingAlgo algorithm= null;

	public Client(String localAddress, int port) {
		try {
			s = new Socket(localAddress, port);
			input = new BufferedReader(new InputStreamReader(s.getInputStream()));
			output = new DataOutputStream(s.getOutputStream());
		} catch (IOException e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}

	// function to read xml file server in the ds-server and return the server arraylist
	public void readXML() {
		servertypeCoreDictionary = new HashMap<String, Integer>();
		try {
			File systemXML = new File("ds-system.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(systemXML);

			doc.getDocumentElement().normalize();
			NodeList servers = doc.getElementsByTagName("server");
			for (int i = 0; i < servers.getLength(); i++) {
				Element server = (Element) servers.item(i);
				String type = server.getAttribute("type");
				int coreCount = Integer.parseInt(server.getAttribute("coreCount"));
				if(!servertypeCoreDictionary.containsKey(type)){
					servertypeCoreDictionary.put(type, coreCount);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
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
	//method to read from server
	public void readFromServer() {
		try {
			// refactor so that this.serverMessage is assigned from input.readLine
			serverMessage = input.readLine();
			receivedNone = serverMessage.equals("NONE");
			//System.out.println("server: " + serverMessage + "\n");
		} catch (IOException e) {
			serverMessage = "Error";
			System.out.println(e);
		}
	}

	//function to read multiples servers from the server message
	private ArrayList<String> readMultiLineFromServer(int numLines) {
		try {
			ArrayList<String> lines = new ArrayList<String>();
			for (int i = 0; i < numLines; i++) {
				serverMessage = input.readLine();
				lines.add(serverMessage);
				receivedNone = serverMessage.equals("NONE");
			}
			//System.out.println("server: "+lines.toString()+"\n");
			return lines;
		} catch (IOException e) {
			serverMessage = "Error";
			System.out.println(e);
			return null;
		}
	}



	//=====================================================================================
	//method to check for an argument
	//implementation for stage 2
	public void checkArgs(String[] argument) {
		boolean flag = false;
		// min cost, max utlise, min turnaround
		List validArgs = Arrays.asList("minc", "maxu", "mint");

		if(argument.length == 0)
		{
			System.out.println("Running without arguments");
			algorithm = new AllToLargest();
		}

		else if (argument.length == 1 || argument.length > 2){
			System.out.println("Invalid argument length");
		}

		else if (!argument[0].equals("-a")) {
			System.out.println("Missing argument: -a");
		}

		else if (!validArgs.contains(argument[1]))
		{
			System.out.println("For argument: -a, Invalid choice: " + (argument[1]));
		}

		else {
			System.out.println("Successful supplied algorithm");
			switch (argument[1]) {
				case "minc":algorithm = new MinCost();break;
				default:System.out.println("Uncaught error");break;
			}
		}
	}

	// main method to do all the client handling
	//Step 1 to Step 3 in the user-guide to initiate the connection
	public void handShake() {
		sendToServer("HELO");
		readFromServer(); // OK

		String username = System.getProperty("user.name");
		String authMessage = "AUTH " + username;
		sendToServer(authMessage);
		readFromServer();// OK
		this.receivedNone = false;
	}

	public void jobSchedule() {
		try {
			//Sending the first REDY to get the first job
			sendToServer("REDY"); // step 5

			String[] serverMessageArray;
			int loopIter = 0;

			//As long as we dont recieve NONE from the server, we keep on sending REDY to to more job
			//and fo more scheduling and we check the NONE keyword in the readFromServer method
			while (!receivedNone) {
				// System.out.println("inside loop: " + loopIter++);
				readFromServer();
				//serverMessageArray is the message we get from the server and split it to schedule the job
				serverMessageArray = serverMessage.split(" ");
				switch (serverMessageArray[0]) {
					case ("JOBN"): // merge with case JOBP
					case "JOBP": {
						Job j = new Job(serverMessageArray);
						sendToServer("GETS Capable " + j.GET());
						readFromServer();//this message from server should be DATA
						int numLines = Integer.parseInt(serverMessage.split(" ")[1]);
						sendToServer("OK");
						ArrayList<String> serverStatuses = readMultiLineFromServer(numLines); // multiple server states
						sendToServer("OK");
						readFromServer(); // .
						System.out.println(serverStatuses + "Server Status\n\n");
						assert serverStatuses != null;
						algorithm.populateServers(serverStatuses);
						algorithm.setTypeCorecountDictionary(this.servertypeCoreDictionary);
						ServerObject serverToScheduleJob = algorithm.getSCHDServer();
						//SCHD the job
						sendToServer("SCHD " + j.jobId + " " + serverToScheduleJob.type +" "+ serverToScheduleJob.id);
						break;
					}
					//when server send a job complete message we send a REDY to get another job
					case "JCPL":

					case "OK": {
						sendToServer("REDY");
						break;
					}

					default:
						break;
				}
			}

			quit();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	//function to send to server to quit
	public void quit() throws IOException {
		sendToServer("QUIT");
		readFromServer();
		if (serverMessage.equals("QUIT")) {
			input.close();
			output.close();
			s.close();
		}
	}

	//the main function to run all these method to create all the scheduling
	public static void main(String[] args) {
		Client client = new Client("127.0.0.1", 50000);
		client.checkArgs(args);
		client.handShake();
		client.readXML();
		client.jobSchedule();
	}
}