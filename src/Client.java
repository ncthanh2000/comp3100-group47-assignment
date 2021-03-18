import java.io.*;
import java.net.*;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Client {



    public static void readXML(){
        try {
			File systemXML = new File("./pre-compiled/ds-system.xml");

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(systemXML);

			doc.getDocumentElement().normalize();
			NodeList servers = doc.getElementsByTagName("server");
			for (int i = 0; i < servers.getLength(); i++) {
				Element server = (Element) servers.item(i);
				String t = server.getAttribute("type");
				int l = Integer.parseInt(server.getAttribute("limit"));
				int b = Integer.parseInt(server.getAttribute("bootupTime"));
				float hr = Float.parseFloat(server.getAttribute("hourlyRate"));
				int c = Integer.parseInt(server.getAttribute("coreCount"));
				int m = Integer.parseInt(server.getAttribute("memory"));
				int d = Integer.parseInt(server.getAttribute("disk"));
				System.out.print(t+"\n");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
    }
	public static void sendToServer(){

	}
    public static void main(String[] args) {
		try {
            String username=System.getProperty("user.name");
            String message = "AUTH " + username;
            Socket s = new Socket("localhost", 50000);

	        BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
            DataOutputStream output = new DataOutputStream(s.getOutputStream());
			
            output.write("HELO".getBytes());
            output.write(message.getBytes());
			readXML();

            output.write("REDY".getBytes());

            output.flush();
            output.close();
            s.close();
			System.out.println("Hello world");
        } catch (Exception e) {
            System.out.println(e);
        }


    }
}