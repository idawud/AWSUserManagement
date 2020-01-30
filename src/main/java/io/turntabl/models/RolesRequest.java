package io.turntabl.models;

import java.util.Set;

public class RolesRequest {
    private String email;
    private String explanation;
    private String request_id;
    private Set<String> awsArns;

    public RolesRequest() { }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<String> getAwsArns() {
        return awsArns;
    }

    public void setAwsArns(Set<String> awsArns) {
        this.awsArns = awsArns;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }
}
