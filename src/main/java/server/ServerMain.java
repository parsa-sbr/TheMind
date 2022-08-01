package server;

import java.io.IOException;

public class ServerMain {
    public static void main(String[] args) {
        try {
            Server server = new Server();
            server.start();
            server.init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
