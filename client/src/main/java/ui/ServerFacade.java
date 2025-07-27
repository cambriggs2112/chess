package ui;

import com.google.gson.Gson;
import model.*;
import java.io.*;
import java.net.*;

public class ServerFacade {
    private String url;
    private Object responseObj;

    public ServerFacade(String url) {
        this.url = url;
    }

    private <T> boolean performRequest(String method, String path, Object request, String authToken, Class<T> resultClass) {
        Gson gson = new Gson();
        try {
            HttpURLConnection http = (HttpURLConnection) new URI(url + path).toURL().openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            if (authToken != null) {
                http.addRequestProperty("Authorization", authToken);
            }
            if (request != null) {
                try (OutputStream out = http.getOutputStream()) {
                    out.write(gson.toJson(request).getBytes());
                }
            }
            http.connect();
            try (InputStream in = http.getInputStream()) {
                if (http.getResponseCode() == 200) {
                    responseObj = gson.fromJson(new InputStreamReader(in), resultClass);
                    return true;
                } else {
                    responseObj = gson.fromJson(new InputStreamReader(in), ErrorResult.class);
                    return false;
                }
            }
        } catch (Exception e) {
            responseObj = new ErrorResult(e.toString());
            return false;
        }
    }
}
