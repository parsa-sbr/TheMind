package server;

import java.io.IOException;

public class ServerMain {
    public static void main(String[] args) {
        try {
            new Server().init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
