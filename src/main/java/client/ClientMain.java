package client;

import java.io.IOException;

public class ClientMain {
    public static void main(String[] args) {
        try {
            new Client().init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}