package com.squidio.javassement.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squidio.javassement.request.StatementRequest;
import com.squidio.javassement.response.Account;
import com.squidio.javassement.response.StatementResponse;
import com.squidio.javassement.response.UnAuthorizedAccess;
import com.squidio.javassement.response.Users;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class AssesementService {
    Logger logger= LoggerFactory.getLogger(AssesementService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public ResponseEntity GetAllUsers(){
        String response=null;
        String url="https://purple-fire-5350.getsandbox.com/users";
        try{
            logger.info("Retrieving All Users from "+url);
            response = restTemplate.getForObject(url,String.class);
        }catch (HttpStatusCodeException e){
            logger.info("Error Retrieving User: "+e.getMessage());
            return ResponseEntity.status(e.getRawStatusCode()).headers(e.getResponseHeaders())
                    .body(e.getResponseBodyAsString());
        }
        return ResponseEntity.ok(response);
    }

    public ResponseEntity GetSpecificUser(String name){
        String url="https://purple-fire-5350.getsandbox.com/users/"+name;
        String response = null;
        try {
            logger.info("Retrieving User with username: "+name+" from "+url);
            response=restTemplate.getForObject(url,String.class);
        }catch (HttpStatusCodeException e){
            logger.info("Error Retrieving User: "+e.getMessage());
            return ResponseEntity.status(e.getRawStatusCode()).headers(e.getResponseHeaders())
                    .body(e.getResponseBodyAsString());
        }

        return ResponseEntity.ok(response);
    }

    public ResponseEntity GetAccountsOwned(String userId, Principal principal) throws JsonProcessingException {
        String personInSession=principal.getName();
        boolean isValidRequest= personInSession.equals("Admin") || (IsIdSameAsSession(personInSession, userId));
        String AccountsUrl="https://purple-fire-5350.getsandbox.com/accounts/"+userId;
        Account[]response=null;
        if(isValidRequest){
            try {
                logger.info("Retrieving Account with userId: "+userId+" from: "+ AccountsUrl);
                response=restTemplate.getForObject(AccountsUrl,Account[].class);
            }catch (HttpStatusCodeException e){
                logger.info("Error Retrieving Account: "+e.getMessage());
                return ResponseEntity.status(e.getRawStatusCode()).headers(e.getResponseHeaders())
                        .body(e.getResponseBodyAsString());
            }

            return ResponseEntity.ok(response);
        }
        else{
          return GetUnAuthorizedMessage();

        }

    }



    public ResponseEntity GetStatements(StatementRequest request, Principal principal){
        String personInSession = principal.getName();
        String url= "https://purple-fire-5350.getsandbox.com/accounts/statements";
        boolean isValid=personInSession.equals("Admin") || (IsAccountIdSameAsSession(personInSession, request.getAccountId()));
        if(isValid){
            //CREATE FORM DATA
            Map<String,String>variables=new HashMap<>();
            variables.put("accountId", request.getAccountId());
            String bodyData=null;
            try {
                bodyData=objectMapper.writeValueAsString(variables);
            } catch (JsonProcessingException e) {
                logger.warn(e.getMessage());
            }
            HttpHeaders headers =new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> requestEntity=new HttpEntity<>(bodyData,headers);

            //CALL URL
            StatementResponse[] response = null;
            try {
                logger.info("Fetching Bank Statement for Account:"+request.getAccountId());
                response=restTemplate.postForObject(url,requestEntity,StatementResponse[].class);
            }catch (HttpStatusCodeException e){
                logger.info("Error Retrieving Statement: "+e.getMessage());
                return ResponseEntity.status(e.getRawStatusCode()).headers(e.getResponseHeaders())
                        .body(e.getResponseBodyAsString());
            }

            //IF FROM DATE TO DATE WAS SPECIFIED RETURN THAT RANGE
            if(isDateRangeAvailable(request) && !isAmountRangeAvailable(request)){
                return DateRange(request,response);
            }
            //IF FROM AMOUNT TO AMOUNT  WAS SPECIFIED RETURN THAT RANGE
            else if(!isDateRangeAvailable(request) && isAmountRangeAvailable(request)){
               return AmountRange(request,response);
            }
            //IF NOTHING IS SPECIFIED RETURN THREE MONTH STATEMENT
            else if(!isDateRangeAvailable(request) && !isAmountRangeAvailable(request)){
               return ThreeMonthStatement(request,response);
            }



        }
            return GetUnAuthorizedMessage();


    }


    private Boolean IsIdSameAsSession(String name, String userId){
        String url="https://purple-fire-5350.getsandbox.com/users/"+name;
        Users response=restTemplate.getForObject(url,Users.class);

        //IF THE ONE REQUESTING IS ADMIN OR THE OWNER OF THE ACCOUNT ,GIVE THEM PRIVILEGES
        return Objects.equals(response.getId(), userId);
    }

    private Boolean IsAccountIdSameAsSession(String name, String accountId){
        String url="https://purple-fire-5350.getsandbox.com/users/"+name;
        //FETCH USER ID
        Users response=restTemplate.getForObject(url,Users.class);
        //USE USER ID TO GET BANK ACCOUNT DETAILS
        String AccountsUrl="https://purple-fire-5350.getsandbox.com/accounts/"+response.getId();
        Account[] accountDetails=restTemplate.getForObject(AccountsUrl,Account[].class);
        //SEARCH IF AN ACCOUNT ID EXISTS FROM THE GIVEN ACCOUNTS
        assert accountDetails != null;
        return Arrays.stream(accountDetails).anyMatch(x->x.getId().equals(accountId));


    }
    private ResponseEntity GetUnAuthorizedMessage() {
        UnAuthorizedAccess message=new UnAuthorizedAccess("Cannot Retrieve Another User's Account");
        String response=null;
        try {
            response=objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
    private Boolean isDateRangeAvailable(StatementRequest request){
        return request.getFromDate() != null && request.getToDate() != null ;
    }
    private Boolean isAmountRangeAvailable(StatementRequest request){
        return request.getFromAmount()!=null && request.getToAmount()!=null ;
    }

    private ResponseEntity DateRange(StatementRequest request,StatementResponse[]response){
            String dr=null;
            List<StatementResponse> dateRangeResponse= Arrays.stream(response)
                    .filter(x->(x.getDate().isAfter(request.getFromDate())|| x.getDate().equals(request.getFromDate()))&& x.getDate().isBefore(request.getToDate()))
                    .collect(Collectors.toList());
            try {
                dr=objectMapper.writeValueAsString(dateRangeResponse);
            } catch (JsonProcessingException e) {
                logger.warn(e.getMessage());
            }
            return ResponseEntity.ok(dr);


    }
    private ResponseEntity AmountRange(StatementRequest request,StatementResponse[]response){
        String amountR=null;
        List<StatementResponse> amountRangeResponse= Arrays.stream(response)
                .filter(x->x.getAmount()>=request.getFromAmount() && x.getAmount()<=request.getToAmount())
                .collect(Collectors.toList());
        try {
            amountR=objectMapper.writeValueAsString(amountRangeResponse);
        } catch (JsonProcessingException e) {
            logger.warn(e.getMessage());
        }
        return ResponseEntity.ok(amountR);


    }

 private ResponseEntity ThreeMonthStatement(StatementRequest request,StatementResponse[]response) {
    String threeMonths = null;
    //GET THE HIGHEST DATE FROM THE LIST FIRST
    LocalDate highestDateInRecord = Arrays.stream(response)
            .map(StatementResponse::getDate)
            .max(Comparator.naturalOrder())
            .get();

    //FETCH A THREE MONTH STATEMENT FROM THE HIGHEST DATE RECORDED
     List<StatementResponse> threeMonthsResponse= Arrays.stream(response)
             .filter(x-> ChronoUnit.MONTHS.between(x.getDate(),highestDateInRecord)<=3)
             .collect(Collectors.toList());

    try {
        threeMonths = objectMapper.writeValueAsString(threeMonthsResponse);
    } catch (JsonProcessingException e) {
        logger.warn(e.getMessage());
    }
    return ResponseEntity.ok(threeMonthsResponse);

}
}
