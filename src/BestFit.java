import java.util.ArrayList;
public class BestFit implements  SchedulingAlgo{
    public ArrayList<ServerObject> servers;
    @Override
    public String getSCHDServer(ArrayList<String> serverStatuses, Job j) {
        return null;
    }


    public void setServers(ArrayList<ServerObject> s){
        this.servers = s;
    };
}
