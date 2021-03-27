import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServerObject {
    int id;

    String type;
    int limit;
    int bootupTime;
    double hourlyRate;
    int coreCount;
    int memory;
    int disk;
    String serverState;
    int curStartTime;

    public ServerObject(String type, int limit, int bootupTime, double hourlyRate, int coreCount, int memory, int disk) {
        this.type = type;
        this.limit = limit;
        this.bootupTime = bootupTime;
        this.hourlyRate = hourlyRate;
        this.coreCount = coreCount;
        this.memory = memory;
        this.disk = disk;
    }

    //this object is for storing the lastest state of the server
    public ServerObject(String type, String id, String serverState, String curStartTime, String coreCount, String memory, String disk) {
        this.type = type;
        this.id = Integer.parseInt(id);
        this.serverState = serverState;
        this.curStartTime = Integer.parseInt(curStartTime);
        this.coreCount = Integer.parseInt(coreCount);
        this.memory = Integer.parseInt(memory);
        this.disk = Integer.parseInt(disk);
    }

    public String toString(){
        return type+", "+limit+", "+bootupTime+", "+hourlyRate+", "+coreCount+", "+memory+", "+disk;
    }
	
	public ServerObject(ServerObject other) {
        this.type = other.type;
        this.limit = other.limit;
        this.bootupTime = other.bootupTime;
        this.hourlyRate = other.hourlyRate;
        this.coreCount = other.coreCount;
        this.memory = other.memory;
        this.disk = other.disk;
    }

	public int compareTo(ServerObject other)
    {
        // Comparison ordering: coreCount -> memory -> disk
        int coreComp = Integer.compare(this.coreCount, other.coreCount);
        int memoryComp = Integer.compare(this.memory, other.memory);
        int diskComp = Integer.compare(this.disk, other.disk);

        return coreComp!=0?coreComp: memoryComp!=0?memoryComp: diskComp;
    }
}
