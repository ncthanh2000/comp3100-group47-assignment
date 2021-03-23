public class ServerObject {
    String type;
    int limit;
    int bootupTime;
    double hourlyRate;
    int coreCount;
    int memory;
    int disk;

    public ServerObject(String type, int limit, int bootupTime, double hourlyRate, int coreCount, int memory, int disk) {
        this.type = type;
        this.limit = limit;
        this.bootupTime = bootupTime;
        this.hourlyRate = hourlyRate;
        this.coreCount = coreCount;
        this.memory = memory;
        this.disk = disk;
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
        // Comparison ordering: type -> coreCount -> memory -> disk
        List<String> typeCompare = Arrays.asList("tiny", "small","medium","large", "xlarge");
        // using typeCompare.indexOf to compare since we know that there's a
        // fixed number of "types"
        int typeComp  = 0;
        if(typeCompare.indexOf(this.type) < typeCompare.indexOf(other.type))
        {
            typeComp = -1;
        }

        if(typeCompare.indexOf(this.type) > typeCompare.indexOf(other.type))
        {
            typeComp = 1;
        }

        int coreComp = Integer.compare(this.coreCount, other.coreCount);
        int memoryComp = Integer.compare(this.memory, other.memory);
        int diskComp = Integer.compare(this.disk, other.disk);

        return typeComp!=0?typeComp: coreComp!=0?coreComp: memoryComp!=0?memoryComp: diskComp;
    }
}
