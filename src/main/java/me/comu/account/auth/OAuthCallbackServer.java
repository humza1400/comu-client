package me.comu.account.auth;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import java.net.InetSocketAddress;
import java.net.URI;
import java.io.OutputStream;

// TODO: implement later, i think we need to sign up for some sort of azure developer account to setup our own client application that way we can parse tokens automatically without user input
public class OAuthCallbackServer {

    private static String authorizationCode;

    public static String listenForCode(int port) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/callback", (HttpExchange exchange) -> {
            URI requestUri = exchange.getRequestURI();
            String query = requestUri.getQuery();
            if (query != null && query.contains("code=")) {
                authorizationCode = extractQueryParam(query, "code");
                String response = "<html><body><h1>Login successful!</h1><p>You can close this window now.</p></body></html>";
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
                server.stop(1);
            }
        });
        server.start();

        while (authorizationCode == null) {
            Thread.sleep(250);
        }

        return authorizationCode;
    }

    private static String extractQueryParam(String query, String key) {
        for (String param : query.split("&")) {
            String[] parts = param.split("=");
            if (parts.length == 2 && parts[0].equals(key)) {
                return parts[1];
            }
        }
        return null;
    }
}
