package dexcom.assignment;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.nio.charset.StandardCharsets;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;

/**
 * Handler for requests to Lambda function.
 */
public class ValidatePassword implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        String requestBody = input.getBody();
        Gson gson = new Gson();

        Map<String,String> userDetails = gson.fromJson(requestBody,Map.class);
        Map returnValue = new HashMap();

        String error = checkPassword(userDetails.get("password"));
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

        if( error.length() == 0) {
            response.withStatusCode(200);
            returnValue.put("hash", hashSHA256(userDetails.get("password")));
        } else {
            returnValue.put("ErrorMessage",error);
            response.withStatusCode(404);
        }

        response.withBody(gson.toJson(returnValue,Map.class));

        Map responseHeaders = new HashMap();
        responseHeaders.put("Content-Type","application/json");

        return response;
    }

    public String checkPassword(String password) {

        if (!((password.length() >= 8)
                && (password.length() <= 255))) {
            return "Password length should be greater than 8 characters";
        }

        if (password.contains(" ")) {
            return "Password contains whitespaces";
        }

        int count = 0;

        for (int i = 0; i <= 9; i++) {
            String str1 = Integer.toString(i);

            if (password.contains(str1)) {
                count = 1;
            }
        }
        if (count == 0) {
            return "Password should contain at least one number";
        }

        if (!(password.contains("@") || password.contains("#")
                || password.contains("!") || password.contains("~")
                || password.contains("$") || password.contains("%")
                || password.contains("^") || password.contains("&")
                || password.contains("*") || password.contains("(")
                || password.contains(")") || password.contains("-")
                || password.contains("+") || password.contains("/")
                || password.contains(":") || password.contains(".")
                || password.contains(", ") || password.contains("<")
                || password.contains(">") || password.contains("?")
                || password.contains("|"))) {
            return "Password should contain at least one special character";
        }

        // The password is valid
        return "";
    }

    public String hashSHA256(String password) {
        MessageDigest digest = null;

        try{
            digest = MessageDigest.getInstance("SHA-256");
        }
        catch(NoSuchAlgorithmException e) {
            System.out.println("Something is wrong");
        }

        byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

        BigInteger noHash = new BigInteger(1, hash);
        String hashStr = noHash.toString(16);

        return hashStr;
    }
}
