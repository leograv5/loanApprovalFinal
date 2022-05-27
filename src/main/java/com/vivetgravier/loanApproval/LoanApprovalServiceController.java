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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@RestController
public class LoanApprovalServiceController {

    private static final String URL_APPROVAL_MANAGER = "urlApprovalManager";
    private static final String URL_ACCOUNT_MANAGER = "https://inf63app8.appspot.com/";
    private static final String URL_CHECK_ACCOUNT = "https://vivetgravier-check-account.herokuapp.com/checkAccount/";

    @RequestMapping(value = "/loanApproval", method = RequestMethod.GET)
    public String loanApproval(@RequestParam(name="name") String name, @RequestParam(name="value") float value) {

        String risk = "LOW", msg = "";
        ObjectMapper objectMapper = new ObjectMapper();
        RestTemplate restTemplate = new RestTemplate();

        /*if (value < 10000) {
            String uriGetRisk = URL_ACCOUNT_MANAGER + "accounts/getRisk?lastname="+name;
            risk = restTemplate.getForObject(uriGetRisk, String.class);
            return risk;
        }*/


        if (risk == "LOW") {
            String uriAddToAccount = URL_ACCOUNT_MANAGER + "accounts/credit";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("lastname", name);
                jsonObject.put("account", value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            HttpEntity<String> request =
                    new HttpEntity<String>(jsonObject.toString(), headers);
            restTemplate.postForEntity(uriAddToAccount, request, String.class);
            return "approved";
        }

        boolean approval = false;
        if (risk == "HIGH" || value >= 10000) {
             //approval = (boolean) restTemplate.getForObject(URL_APPROVAL_MANAGER, Boolean.class);
        }

        if (approval) {
            return "approved";
        } else {
            return "refused";
        }
    }
}
