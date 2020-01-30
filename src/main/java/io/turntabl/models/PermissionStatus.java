package io.turntabl.models;

public class PermissionStatus {
    private String status;
    private String Message;

    public PermissionStatus() { }

    public PermissionStatus(boolean state) {
        if (state){
            this.status = "Successful";
        }else {
            this.status = "Not Successful";
        }
    }

    public PermissionStatus(boolean state, String msg) {
        if (state){
            this.status = "Successful";
        }else {
            this.status = "Not Successful";
        }
        setMessage(msg);
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
