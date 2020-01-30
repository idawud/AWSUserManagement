package io.turntabl.services;


import io.turntabl.models.PendingRequest;
import io.turntabl.models.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class PermissionStorage {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public PermissionStorage(){ }

    public long insert(String userEmail, Set<String> arnsRequest){
        String arnsString = String.join(" -,,- ", arnsRequest);

        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate).withTableName("requests").usingGeneratedKeyColumns("id");
        Map<String, Object> insertValue = new HashMap<>();
        insertValue.put("status", "PENDING");
        insertValue.put("useremail", userEmail);
        insertValue.put("arn", arnsString);

        Number number = insert.executeAndReturnKey(insertValue);
        return number.longValue();
    }



    public void approvedRequest( long requestId){
        jdbcTemplate.update("UPDATE requests SET status = 'APPROVED', approvedtime = ? " +
        " WHERE id = ? ", LocalDateTime.now(), requestId
        );
    }

    public String removeRequest( long requestId) {
        String userEmail = getRequestDetails(requestId).getUserEmail();
        jdbcTemplate.update("DELETE FROM requests WHERE id = ?", requestId);
        return userEmail;
    }

    public Request getRequestDetails( long requestId) {
        return jdbcTemplate.queryForObject("SELECT * FROM requests WHERE id = ?", new Object[]{requestId},
                BeanPropertyRowMapper.newInstance(Request.class));
    }

    public List<Request> approvedPermissions(){
        return jdbcTemplate.query("SELECT * FROM requests WHERE status = 'APPROVED'",
                BeanPropertyRowMapper.newInstance(Request.class));
    }

    public List<PendingRequest> pendingRequests(){
         return jdbcTemplate.query("SELECT * FROM requests WHERE status = 'PENDING'",
                BeanPropertyRowMapper.newInstance(Request.class))
                 .stream()
                    .map(q -> {
                        Set<String> awsArns = new HashSet<>(Arrays.asList(q.getARN().split(" -,,- ")));
                        return new PendingRequest( q.getUserEmail(), q.getStatus(), q.getIdentifier(), awsArns);
                    })
                    .collect(Collectors.toList());
    }
}

