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

	public Client(String localAddress, int port) {

		try {
			s = new Socket("localhost", 50000);
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

	// function to read xml file server from the ds-server
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
			output.write(message.getBytes());
			output.flush();

		} catch (IOException e) {
			System.out.println(e);
		}
	}
	
	//main method to do all the client handling
	public void start() {
		try {
			String username = System.getProperty("user.name");
			String authMessage = "AUTH " + username;

			sendToServer("HELO");
			sendToServer(authMessage);
			sendToServer("REDY");

			ArrayList<ServerObject> serverList=readXML();

			System.out.println(serverList.toString());
			
			// sendToServer("REDY");

			s.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
			System.out.println("Successful function call");
			flag = true;
		}
		return flag;
	}

	public static void main(String[] args) {
		Client client = new Client("127.0.0.1", 50000);
		client.checkArgs(args);
		// boolean validArg = checkArgs(args);
		// DO SOME ARGUMENT CHECKING
		client.start();
		System.out.println("Hello world");

	}
}