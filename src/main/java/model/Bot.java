package model;

import server.Game;

import java.util.Vector;

public class Bot extends Thread{

    Vector<Integer> cards;
    String username;
    boolean play = false;
    Game game;

    public Bot(String username) {
        this.username = username;
        cards = new Vector<>();
    }

    @Override
    public void run() {
        while (true) {
            if (play) {
                try {
                    long waitingTime = (cards.get(0) - game.getCardOnTable()) * 300L;
                    Thread.sleep(waitingTime);
                    game.playCard(username, cards.get(0));

                } catch (Exception e) {

                }
            }
        }
    }

    public Vector<Integer> getCards() {
        return cards;
    }

    public void setCards(Vector<Integer> cards) {
        this.cards = cards;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void startPlaying() {
        play = true;
    }

    public void stopPlaying() {
        play = false;
        this.interrupt();
    }

}
