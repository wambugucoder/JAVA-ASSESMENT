package com.squidio.javassement.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.squidio.javassement.request.StatementRequest;
import com.squidio.javassement.service.AssesementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.security.Principal;

@RestController
@Validated
public class AssesementController {
    @Autowired
    private AssesementService assesementService;


    @GetMapping(value = "/api/v1/admin/assesement/users",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity RetrieveUsers(){
    return assesementService.GetAllUsers();
    }
    @GetMapping(value = "/api/v1/admin/assesement/users/{name}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity RetrieveSpecificUsers(@PathVariable String name, Principal principal){
        return assesementService.GetSpecificUser(name);
    }
    @GetMapping(value = "/api/v1/accounts/assesement/users/{userId}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity RetrieveAccounts(@PathVariable String userId, Principal principal) throws JsonProcessingException {
        return assesementService.GetAccountsOwned(userId,principal);
    }
    @PostMapping(value = "/api/v1/accounts/assesement/statements",produces = MediaType.APPLICATION_JSON_VALUE,consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity RetrieveStatements(@RequestBody StatementRequest request, Principal principal) {
        return assesementService.GetStatements(request,principal);
    }
}
