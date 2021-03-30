public class Jobs {
    int submitTime;
    int jobId;
    int estRuntime;
    int core;
    int memory;
    int disk;

    public Jobs(int submitTime, int jobId, int estRuntime, int core, int memory, int disk) {
        this.submitTime=submitTime;
        this.jobId=jobId;
        this.estRuntime=estRuntime;
        this.core=core;
        this.memory=memory;
        this.disk=disk;
    }
}
