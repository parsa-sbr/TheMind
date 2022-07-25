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

    public Client() throws IOException {
        socket = new Socket("localhost", 1234);
        scan = new Scanner(System.in);
        in = new Scanner(socket.getInputStream());
        out = new PrintWriter(socket.getOutputStream());
    }

    public void init() {
        Thread read = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    String input = in.nextLine();
                    System.out.println(input);
                }
            }
        });

        Thread write = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    String send = scan.nextLine();
                    out.println(send);
                    out.flush();
                }
            }
        });

        read.start();
        write.start();
    }
}
