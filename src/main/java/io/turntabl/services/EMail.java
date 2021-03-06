 package io.turntabl.services;


import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Set;
import java.util.stream.Collectors;

public class EMail {
    public static void requestMessage(String requestIdentifier, String userName, String userEmail , Set<String> roles,String explanation, String requestId) throws IOException, GeneralSecurityException {
        if ( roles.size() == 0){ return;}

        String subject = "[" + requestIdentifier + "] Request for AWS Role Permission";
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

        if ( !explanation.isEmpty()){
            body.append("<h4> Explanation: </h4>" );
            body.append(" <p style=\"font-size: 14px;\">").append(explanation).append("</p>");
        }

        body.append("<a href=\"https://permission.services.turntabl.io/v1/api/aws-mgnt/approve/")
                .append(requestId).append("\" target=\"_self\" class=\"button\" style=\"background-color: #4CAF50;border: none;color: white;padding: 10px 22px;text-align: center;text-decoration: none;display: inline-block;font-size: 14px;border-radius: 12px\">Approve</a>       \n").append(" \n")
                .append(" <a href=\"https://permission.services.turntabl.io/v1/api/aws-mgnt/decline/").append(requestId).append("\" target=\"_blank\" class=\"button button3\" style=\"background-color: #f44336;border: none;color: white;padding: 10px 22px;text-align: center;text-decoration: none;display: inline-block;font-size: 14px;border-radius: 12px\">Decline</a> ");

        GmailService.sendMail(userEmail, System.getenv("GSUITE_ADMIN_EMAIL"), subject, body.toString());
    }


    public static void feedbackMessage(String userEmail, String requestIdentifier, Boolean granted) throws IOException, GeneralSecurityException {
        String subject, body;
        if (granted) {
            subject = "[" + requestIdentifier + "] AWS Role Permission Request Granted";
            body = " <p style=\"font-size: 16px;\"> This permissions will last for only "+ System.getenv("PERMISSION_DURATION_IN_MINUTES") + "min. </p>" ;
        }else {
            subject = "AWS Role Permission Request Declined";
            body = " <p style=\"font-size: 16px;\"> Sorry, your request has being declined, contact the administrator sam@turntabl.io</p>";
        }

        GmailService.sendMail(System.getenv("GSUITE_ADMIN_EMAIL"), userEmail, subject, body);
    }

}
