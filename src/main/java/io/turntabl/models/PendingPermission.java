package io.turntabl.models;

import java.util.Set;

public class PendingPermission {
    private String userEmail;
    private String status;
    private Set<String> awsArns;

    public PendingPermission(String userEmail, String status, Set<String> awsArns) {
    }

    public PendingPermission() {
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
}
