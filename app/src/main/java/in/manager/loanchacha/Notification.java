package in.manager.loanchacha;

/**
 * Created by chhapoliya on 21/01/16.
 */
public class Notification {
    private String id;
    private String msg;
    private String ontime;

    // Constructor for the Phonebook class
    public Notification(String id, String msg, String ontime) {
        super();
        this.id = id;
        this.msg = msg;
        this.ontime = ontime;
    }

    // Getter and setter methods for all the fields.
    // Though you would not be using the setters for this example,
    // it might be useful later.
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public String getOntime() {
        return ontime;
    }
    public void setMail(String ontime) {
        this.ontime = ontime;
    }
}