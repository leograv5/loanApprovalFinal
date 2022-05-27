package com.vivetgravier.loanApproval;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.coyote.Response;
import org.apache.tomcat.util.json.JSONParser;
import org.json.JSONException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.json.JSONObject;

import java.net.URI;
import java.net.URL;
import java.util.*;

@RestController
public class LoanApprovalServiceController {

    private static final String URL_APPROVAL_MANAGER = "https://approval-manager-dot-inf63app8.appspot.com/approvals/";
    private static final String URL_ACCOUNT_MANAGER = "https://account-manager-dot-inf63app8.appspot.com/accounts/";

    @RequestMapping(value = "/loanApproval", method = RequestMethod.GET)
    public String loanApproval(@RequestParam(name="name") String name, @RequestParam(name="value") double value) {

        String risk = "";


        if (value < 10000) {
            RestTemplate restTemplate = new RestTemplate();
            String uriGetRisk = URL_ACCOUNT_MANAGER + "getRisk?lastname="+name;
            try {
                risk = restTemplate.getForObject(uriGetRisk, String.class);
            } catch (Exception e) {
                return "Le compte n'existe pas";
            }
        }


        if (Objects.equals(risk, "LOW")) {
            try {
                addToAccount(name, value);
            } catch (Exception e) {
                return "Le compte demandé est inexistant";
            }

            return "approved";
        }

        boolean approval = false;
        if (Objects.equals(risk, "HIGH") || value >= 10000) {
            String uriCreateApproval = URL_APPROVAL_MANAGER + "add?lastname="+name+"&amount="+value;
            RestTemplate restTemplate = new RestTemplate();
            approval = restTemplate.postForObject(uriCreateApproval, "", boolean.class);
        }

        if (approval) {
            try {
                addToAccount(name, value);
            } catch (Exception e) {
                return "Le compte demandé est inexistant";
            }
            return "approved";
        }

        return "refused";
    }

    private void addToAccount(String lastname, double account) throws Exception{
        RestTemplate restTemplate = new RestTemplate();
        String uriAddToAccount = URL_ACCOUNT_MANAGER + "credit";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("lastname", lastname);
            jsonObject.put("account", account);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpEntity<String> request = new HttpEntity<String>(jsonObject.toString(), headers);
        restTemplate.postForEntity(uriAddToAccount, request, String.class);
    }
}
