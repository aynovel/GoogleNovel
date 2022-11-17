package life.forever.cf.entry;


public class ReadTaskBean {

    private boolean isReceive;


    private String taskId;

    private int alreadyReadTime;

    public boolean isReceive() {
        return isReceive;
    }

    public void setReceive(boolean receive) {
        isReceive = receive;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public int getAlreadyReadTime() {
        return alreadyReadTime;
    }

    public void setAlreadyReadTime(int alreadyReadTime) {
        this.alreadyReadTime = alreadyReadTime;
    }
}
