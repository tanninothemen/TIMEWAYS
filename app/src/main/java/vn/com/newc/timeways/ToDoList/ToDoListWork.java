package vn.com.newc.timeways.ToDoList;

public class ToDoListWork {
    public String workID;
    public String WorkContent;
    public String WorkTimeDue;
    public String workDateDue;
    public boolean workStatus;

    public ToDoListWork() {
    }

    public ToDoListWork(String workID, String workContent, boolean workStatus) {
        this.workID = workID;
        WorkContent = workContent;
        this.workStatus = workStatus;
    }

    public ToDoListWork(String workID, String workContent, String workDateDue, boolean workStatus) {
        this.workID = workID;
        WorkContent = workContent;
        this.workDateDue = workDateDue;
        this.workStatus = workStatus;
    }

    public ToDoListWork(String workID, String workContent, String workTimeDue, String workDateDue, boolean workStatus) {
        this.workID = workID;
        WorkContent = workContent;
        WorkTimeDue = workTimeDue;
        this.workDateDue = workDateDue;
        this.workStatus = workStatus;
    }


}
