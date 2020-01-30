package io.turntabl.models;

import java.util.Set;

public class PendingRequest {
    private String userEmail;
    private String status;
    private String identifier;
    private String request_time;
    private Set<String> awsArns;

    public PendingRequest(String userEmail, String status, String identifier, String request_time, Set<String> awsArns) {
        this.userEmail = userEmail;
        this.status = status;
        this.identifier = identifier;
        this.awsArns = awsArns;
        this.setRequest_time(request_time);
    }

    public PendingRequest() {
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Set<String> getAwsArns() {
        return awsArns;
    }

    public void setAwsArns(Set<String> awsArns) {
        this.awsArns = awsArns;
    }

    public String getRequest_time() {
        return request_time;
    }

    public void setRequest_time(String request_time) {
        if ( request_time.contains(".")) {
            this.request_time = request_time.substring(0, request_time.lastIndexOf("."));
        }else {
            this.request_time = request_time;
        }
    }
}
