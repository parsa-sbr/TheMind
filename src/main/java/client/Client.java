package client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    Socket socket;
    Scanner scan;
    Scanner in;
    PrintWriter out;
    String token;
    boolean running = true;

    public Client() throws IOException {
        socket = new Socket("localhost", 1234);
        scan = new Scanner(System.in);
        in = new Scanner(socket.getInputStream());
        out = new PrintWriter(socket.getOutputStream());
    }

    public void init() {
        Thread read = new Thread(() -> {
            while (running) {
                String input = in.nextLine();
                if (input.equals("TERMINATE")) {
                    running = false;
                    continue;
                }
                if (input.startsWith("TOKEN")) {
                    token = input.split("#")[1];
                    continue;
                }
                System.out.println(input);
            }

            try {
                socket.close();
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Thread write = new Thread(() -> {
            while (running) {
                String send = scan.nextLine();
                if (token != null) {
                    send = send + "#" + token;
                }
                out.println(send);
                out.flush();
            }
        });
        read.start();
        write.start();
    }

    public void setToken(String token) {
        this.token = token;
    }
}