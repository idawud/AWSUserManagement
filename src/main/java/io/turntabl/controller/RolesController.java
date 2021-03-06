 package io.turntabl.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.turntabl.models.*;
import io.turntabl.services.EMail;
import io.turntabl.services.GSuite;
import io.turntabl.services.PermissionStorage;
import io.turntabl.services.Roles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Api
@RestController
@CrossOrigin(origins = "*")
public class RolesController {
    @Autowired
    private PermissionStorage permissionStorage;

    @ApiOperation("user submits list of aws arns to be given access to..")
    @PostMapping(value = "/v1/api/aws-mgnt/send", consumes = "application/json", produces = "application/json")
    public PermissionStatus sendPermission(@RequestBody RolesRequest rolesRequest){
        try {
            long insertId = permissionStorage.insert(rolesRequest.getIdentifier(), rolesRequest.getEmail() ,rolesRequest.getAwsArns());
            if ( insertId == -11){
                return new PermissionStatus(false, "Operation halted, Submitting an empty request");
            }
            String username = GSuite.fetchEmailToUserName().getOrDefault(rolesRequest.getEmail(), "");
            if (!username.isEmpty()){
                EMail.requestMessage( rolesRequest.getIdentifier(), username, rolesRequest.getEmail(), rolesRequest.getAwsArns(), rolesRequest.getExplanation(), String.valueOf(insertId));
                return new PermissionStatus(true, "request submitted, pending approval");
            }
            return new PermissionStatus(false, "Invalid User");
        }catch (Exception e){
            e.printStackTrace();
            return new PermissionStatus(false, "error sending mail");
        }
    }

    @ApiOperation("approve a request to gain permission to a set of services")
    @GetMapping(value = "/v1/api/aws-mgnt/approve/{requestId}", produces = "application/json")
    public PermissionStatus approve(  @PathVariable("requestId") long requestId ){
        try {
            Request requestDetails = permissionStorage.getRequestDetails(requestId);
            if ( requestDetails  == null){
                return new PermissionStatus(false, "permission Granted or Declined Already");
            }

            permissionStorage.approvedRequest( requestId );

            String userEmail = requestDetails.getUserEmail();
            List<String> strings = Arrays.asList(requestDetails.getARN().split(" -,,- "));
            Set<String> awsArns = new HashSet<>(strings);

           GSuite.grantMultipleAWSARN(userEmail, awsArns);
           EMail.feedbackMessage(userEmail, requestDetails.getIdentifier(), true);

            return new PermissionStatus(true);
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            return new PermissionStatus(false,"error sending mail");
        }
    }

    @ApiOperation("decline a request to gain permission to a set of services")
    @GetMapping(value = "/v1/api/aws-mgnt/decline/{requestId}", produces = "application/json")
    public PermissionStatus decline(  @PathVariable("requestId") long requestId ){
        try {
            Request requestDetails = permissionStorage.getRequestDetails(requestId);
            if ( requestDetails  == null){
                return new PermissionStatus(false, "permission Granted or Declined Already");
            }

            String email = permissionStorage.removeRequest(requestId);
            EMail.feedbackMessage(email, requestDetails.getIdentifier(),false);
            return new PermissionStatus(true);
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            return new PermissionStatus(false,"error sending email");
        }
    }

    @ApiOperation("get all pending requests waiting for approval")
    @GetMapping(value = "/v1/api/aws-mgnt/pending", produces = "application/json")
    public List<PendingRequest> getAllPendingRequests(){
        return permissionStorage.pendingRequests();
    }

    @ApiOperation("get all roles on the aws account")
    @GetMapping(value = "/v1/api/aws-mgnt/roles", produces = "application/json")
    public List<BasicRole> allRoles(){
        return Roles.getAllAvailableRoles();
    }

    @ApiOperation("get all users who are engineer on the gsuite account")
    @GetMapping(value = "/v1/api/aws-mgnt/users", produces = "application/json")
    public List<UserProfileLight> getAllUsers(){
        return  GSuite.fetchAllUsers();
    }

}
