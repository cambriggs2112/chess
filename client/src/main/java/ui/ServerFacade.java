package ui;

import com.google.gson.Gson;
import model.*;
import java.io.*;
import java.net.*;

public class ServerFacade {
    private String url;

    public ServerFacade(String url) {
        this.url = url;
    }

    public void clearApplication() throws ServiceException {
        performRequest("/db", "DELETE", null, null, ClearApplicationResult.class);
    }

    public CreateGameResult createGame(CreateGameRequest req) throws ServiceException {
        return performRequest("/game", "POST", req, req.authToken(), CreateGameResult.class);
    }

    public void joinGame(JoinGameRequest req) throws ServiceException {
        performRequest("/game", "PUT", req, req.authToken(), JoinGameResult.class);
    }

    public ListGamesResult listGames(ListGamesRequest req) throws ServiceException {
        return performRequest("/game", "GET", null, req.authToken(), ListGamesResult.class);
    }

    public LoginResult login(LoginRequest req) throws ServiceException {
        return performRequest("/session", "POST", req, null, LoginResult.class);
    }

    public void logout(LogoutRequest req) throws ServiceException {
        performRequest("/session", "DELETE", null, req.authToken(), LogoutResult.class);
    }

    public RegisterResult register(RegisterRequest req) throws ServiceException {
        return performRequest("/user", "POST", req, null, RegisterResult.class);
    }

    private <T> T performRequest(String path, String method, Object request, String authToken, Class<T> resultClass) throws ServiceException {
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
            System.out.println("test");
            int httpCode = http.getResponseCode();
            System.out.println("test");
            try (InputStream in = http.getInputStream()) {
                System.out.println("test");
                if (httpCode != 200) {
                    System.out.println("test");
                    ErrorResult res = gson.fromJson(new InputStreamReader(in), ErrorResult.class);
                    System.out.println("test");
                    throw new ServiceException(res.message(), httpCode);
                }
                System.out.println("test");
                return gson.fromJson(new InputStreamReader(in), resultClass);
            }
        } catch (URISyntaxException | IOException e) {
            throw new ServiceException(e.toString(), 500);
        }
    }
}
