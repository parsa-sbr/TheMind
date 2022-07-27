package model;

import server.Game;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;

public class ClientHandler extends Thread{

    Vector<Integer> cards = new Vector<>();
    Socket socket;
    Scanner in;
    PrintWriter out;
    String name;
    Game game;

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        in = new Scanner(socket.getInputStream());
        out = new PrintWriter(socket.getOutputStream());
    }

    public Vector<Integer> getCards() {
        return cards;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }

    public void sendMessage(String text) {
        out.println(text);
        out.flush();
    }






    @Override
    public void run() {
        while (true) {
            String action = in.nextLine();
            if (action.equals("p")) {
                Integer card = getCards().get(0);
                game.setCardOnTable(card);
                getCards().remove(card);
            }
        }
    }
}
