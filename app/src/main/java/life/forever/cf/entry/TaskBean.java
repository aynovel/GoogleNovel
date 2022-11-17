package life.forever.cf.entry;


public class TaskBean {
    public String serverNo;
    public long serverTime;
    public ResultData resultData;

    public  class ResultData{
        public String msg;
        public long status;
        public TaskDetailBean lists;
    }
}
