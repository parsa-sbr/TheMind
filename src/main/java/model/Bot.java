package model;

import server.Game;

import java.util.Vector;

public class Bot extends Thread{
    Vector<Integer> cards;
    Game game;

    public Bot() {
       cards = new Vector<>();
    }


    public Vector<Integer> getCards() {
        return cards;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void play() throws InterruptedException {

        //*** doesn't work, just testing...
        int time = (1/(getCards().get(0) - game.getCardOnTable())) * 20000;
        Thread.sleep(5000);
        Integer card = getCards().get(0);
        game.setCardOnTable(card);
        getCards().remove(card);
    }

    @Override
    public void run() {
        while (true) {

            try {
                play();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
