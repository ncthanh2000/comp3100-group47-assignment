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
}
