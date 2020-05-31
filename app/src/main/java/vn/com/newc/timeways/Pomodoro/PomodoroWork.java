package vn.com.newc.timeways.Pomodoro;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class PomodoroWork {
    public String workName;
    public String workDate;
    public int workSession;

    public PomodoroWork() {
    }

    public PomodoroWork(String workName, String workDate, int workSession) {
        this.workName = workName;
        this.workDate = workDate;
        this.workSession = workSession;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("workName", workName);
        result.put("workDate", workDate);
        result.put("workSession", workSession);

        return result;
    }
}
