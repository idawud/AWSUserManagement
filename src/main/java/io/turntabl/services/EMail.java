 package io.turntabl.services;


import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Set;
import java.util.stream.Collectors;

public class EMail {
    public static void requestMessage(String userName, String userEmail , Set<String> roles, String requestId) throws IOException, GeneralSecurityException {
        final String BACKEND_URL = System.getenv("SERVER_URL"); // https://services-1305239961.us-west-2.elb.amazonaws.com/permission/
        if ( roles.size() == 0){ return;}

        String subject = "Request for AWS Role Permission";
        StringBuilder body = new StringBuilder( " <p style=\"font-size: 14px;\">" + userName + " ( " + userEmail  + " ) <br>");
        if ( roles.size() > 1){
            body.append(" is seeking permission to the following AWS roles: </p>");
            body.append("<ul style=\"font-size: 14px;\">");
            roles.stream().map(str -> {
                String[] inter = str.split("/");
                return inter[inter.length - 1];
            }).collect(Collectors.toSet()).forEach( role -> body.append("<li>").append(role).append("</li>"));
            body.append("</ul> <br>" );

        }
        else {
            String[] inter = roles.stream().findFirst().get().split("/");
            body.append(" is seeking permission for ").append(inter[inter.length - 1]).append("  AWS role. </p> <br>");
        }

        body.append("<a href=\"https://accounts.google.com/o/oauth2/v2/auth?scope=openid%20email&access_type=offline&include_granted_scopes=true&state=").append(requestId).append("&redirect_uri=").append(BACKEND_URL).append("approve/").append("&response_type=code&client_id=").append(System.getenv("OPENIDC_KEY")).append("&hd=turntabl.io\" target=\"_self\" class=\"button\" style=\"background-color: #4CAF50;border: none;color: white;padding: 10px 22px;text-align: center;text-decoration: none;display: inline-block;font-size: 16px;border-radius: 12px\">Approve</a> ");
        body.append("<a href=\"https://accounts.google.com/o/oauth2/v2/auth?scope=openid%20email&access_type=offline&include_granted_scopes=true&state=").append(requestId).append("&redirect_uri=").append(BACKEND_URL).append("decline/").append("&response_type=code&client_id=").append(System.getenv("OPENIDC_KEY")).append("&hd=turntabl.io\" target=\"_blank\" class=\"button button3\" style=\"background-color: #f44336;border: none;color: white;padding: 10px 22px;text-align: center;text-decoration: none;display: inline-block;font-size: 16px;border-radius: 12px\">Decline</a>\n");

        GmailService.sendMail(System.getenv("GSUITE_ADMIN_EMAIL"), userEmail, subject, body.toString());
    }


    public static void feedbackMessage(String userEmail, Boolean granted) throws IOException, GeneralSecurityException {
        String subject, body;
        if (granted) {
            subject = "AWS Role Permission Request Granted";
            body = " <p style=\"font-size: 16px;\"> This permissions will last for only "+ System.getenv("PERMISSION_DURATION_IN_MINUTES") + "min. </p>" ;
        }else {
            subject = "AWS Role Permission Request Declined";
            body = " <p style=\"font-size: 16px;\"> Sorry, your request has being declined, contact the administrator sam@turntabl.io</p>";
        }

        GmailService.sendMail(userEmail, System.getenv("GSUITE_ADMIN_EMAIL"), subject, body);
    }

}