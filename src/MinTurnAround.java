import java.util.ArrayList;

public class MinTurnAround implements SchedulingAlgo {
    public ArrayList<ServerObject> servers;
    public void populateServers(ArrayList<String> serverStatuses){
        for (String s : serverStatuses) {
            String[] sArray = s.split(" ");
            ServerObject sObj = new ServerObject(sArray);
            this.servers.add(sObj);
        }
    }
    public ServerObject getLargestServer(){
        ServerObject largest = servers.get(0);
        for(ServerObject s: servers){
            if(largest.compareTo(s) < 0){
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
    public ServerObject getSCHDServer() {
        return this.getLargestServer(); // something like "super-silk 0"
    }
}
