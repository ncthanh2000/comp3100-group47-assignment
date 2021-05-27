import java.util.ArrayList;
import java.util.Map;

public class MinCost implements SchedulingAlgo {
    public ArrayList<ServerObject> servers;
    public Map<String, Integer> typeCorecountDict;
    public void populateServers(ArrayList<String> serverStatuses){
        servers = new ArrayList<ServerObject>();
        for (String s : serverStatuses) {
            String[] sArray = s.split(" ");
            ServerObject sObj = new ServerObject(sArray);
            this.servers.add(sObj);
        }
    }
    //Getting the smallest server to run on the job first
    //so that we can save the cost because the smaller server cost much less
    public ServerObject getSmallestServer(){
        ServerObject smallest = servers.get(0);
        String smallestType = smallest.type;

        for(ServerObject s: servers){
            if(s.type.equals(smallestType) && smallest.compareTo(s) > 0){
                smallest = s;
            }

//            else if(smallest.compareTo(s) == 0){
//                // same coreCount from GETS capable, so check type
//                if(typeCorecountDict.get(smallest.type) > typeCorecountDict.get(s.type)){
//                    smallest = s;
//                }
//            }
        }
        System.out.println(smallest + "Smallest Server");
        return smallest;
    }

    @Override
    public void setServers(ArrayList<ServerObject> s) {
        servers = s;
    }

    @Override
    public ServerObject getSCHDServer() {
        return this.getSmallestServer(); // something like "super-silk 0"
    }

    @Override
    public void setTypeCorecountDictionary(Map<String, Integer> servertypeCoreDictionary) {
        typeCorecountDict = servertypeCoreDictionary;
    }
}