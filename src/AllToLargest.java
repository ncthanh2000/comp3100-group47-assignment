import java.util.ArrayList;

public class AllToLargest implements SchedulingAlgo{
    public ArrayList<ServerObject> servers;

    //method to get the largest server
    public ServerObject getLargestServer() {
        ServerObject max = servers.get(0);
        for (ServerObject s : servers) {
            if (max.compareTo(s) < 0) {
                max = s;
            }
        }
        return new ServerObject(max);
    }

    @Override
    public String getSCHDServer(ArrayList<String> serverStatuses, Job j) {
        // assuming the arraylist servers have been initialised
        // serverState is sent as 1 message in page 15 of ds-sim-user-guide

        // note that, while there might be unavailable servers,
        // the client does not have to handle
        // "scheduling to only available/booting.." servers.
        // in later implementations, we could improve on this

        String largestType = getLargestServer().type;
        int id = 0;
        for (String s : serverStatuses) {
            String[] splitted = s.split(" ");
            if (splitted[0].equals(largestType)) {
                id = Math.min(Integer.parseInt(splitted[1]), id);
            }
        }
        return largestType + " " + id; // something like "super-silk 0"
    }

    public void setServers(ArrayList<ServerObject> s){
        this.servers = s;
    }
}
