package vn.com.newc.timeways.Eisenhower;

public class EisenhowerWork {
    public String workID;
    public String workName;
    public String workType;
    public String workDateStart;
    public String workDateEnd;

    public EisenhowerWork() {
    }

    public EisenhowerWork(String workID, String workName, String workType, String workDateStart, String workDateEnd) {
        this.workID = workID;
        this.workName = workName;
        this.workType = workType;
        this.workDateStart = workDateStart;
        this.workDateEnd = workDateEnd;
    }
}
