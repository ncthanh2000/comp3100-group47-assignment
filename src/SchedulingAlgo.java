import java.util.ArrayList;

public interface SchedulingAlgo {
    // some methods that are common with scheduling algo
    //take an input list of servers, and return another list of servers
    // that meet a certain condition depending on the algorithm
    public void setServers(ArrayList<ServerObject> s);
    public void populateServers(ArrayList<String> serverStatuses);
    public ServerObject getSCHDServer();
}
