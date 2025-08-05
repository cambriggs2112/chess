package ui;

import javax.websocket.*;
import java.net.*;
import java.io.*;

public class WebSocketClient extends Endpoint {
    private Session session;

    public WebSocketClient() throws URISyntaxException, DeploymentException, IOException {
        this.session = ContainerProvider.getWebSocketContainer().connectToServer(this, new URI("ws://localhost:8081/ws"));
        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                // do something with message
            }
        });
    }

    public void onOpen(Session session, EndpointConfig endPointConfig) {}
}
