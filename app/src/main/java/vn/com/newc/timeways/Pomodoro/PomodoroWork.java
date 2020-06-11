package vn.com.newc.timeways.Pomodoro;

public class PomodoroWork {
    public String workID;
    public String workName;
    public String workDate;
    public String workTime;
    public int workSession;

    public PomodoroWork() {
    }

    public PomodoroWork(String workID, String workName, String workDate, String workTime, int workSession) {
        this.workID = workID;
        this.workName = workName;
        this.workDate = workDate;
        this.workTime = workTime;
        this.workSession = workSession;
    }
}
