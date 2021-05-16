import java.lang.reflect.Array;
import java.util.ArrayList;

public class MinTurnAround implements SchedulingAlgo {
    public ArrayList<ServerObject> servers;

    public ServerObject getLargestServer() {
        ServerObject max = servers.get(0);
        for (ServerObject s : servers) {
            if (max.compareTo(s) < 0) {
                max = s;
            }
        }
        return new ServerObject(max);
    }
    public String getLargestServerId(ArrayList<String> serverStatuses){
        
        String largest = serverStatuses.get(0);

        for (String s : serverStatuses) {
            System.out.println(s + "each server\n\n");

            String[] splittedLargest = largest.split(" ");
            String[] splitted = s.split(" ");
            if (Integer.parseInt(splitted[4])>=Integer.parseInt(splittedLargest[4])) {
                largest = s;
            }
        }
        return largest;
    }

    @Override
    public void setServers(ArrayList<ServerObject> s) {
        servers = s;
    }

    @Override
    public String getSCHDServer(ArrayList<String> serverStatuses, Job j) {
        String LargestServer = getLargestServerId(serverStatuses);
        System.out.println(LargestServer + "Largest server respond");
        String largestType = getLargestServer().type;
        int id = 0;
        for (String s : serverStatuses) {
            System.out.println(s + "each server\n\n");


            String[] splitted = s.split(" ");
            if (splitted[0].equals(largestType)) {
                id = Math.min(Integer.parseInt(splitted[1]), id);
            }
        }
        return largestType + " " + id; // something like "super-silk 0"
    }
}
