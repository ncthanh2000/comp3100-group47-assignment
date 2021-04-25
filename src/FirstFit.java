import java.util.ArrayList;

public class FirstFit implements SchedulingAlgo {
    public ArrayList<ServerObject> servers;

    @Override
    public void setServers(ArrayList<ServerObject> s) {
        servers = s;
    }

    @Override
    public String getSCHDServer(ArrayList<String> serverStatuses, Job j) {
        return null;
    }
}
