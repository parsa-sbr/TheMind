package model;

import server.Game;

import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public class Bot extends Thread{
    Vector<Integer> cards;
    Game game;
    String username;
    AtomicBoolean play = new AtomicBoolean(false);
    AtomicBoolean stopped = new AtomicBoolean(false);

    public Bot(String username) {
       this.username = username;
       cards = new Vector<>();
    }

    public AtomicBoolean getStopped() {
        return stopped;
    }

    public Vector<Integer> getCards() {
        return cards;
    }

    public void setCards(Vector<Integer> cards) {
        this.cards = cards;
    }

    public AtomicBoolean getPlay() {
        return play;
    }

    public String getUsername() {
        return username;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    public void run() {
            while (game.getGameIsAlive().get() && !stopped.get()) {
                if (play.get() && getCards().size() != 0) {
                    try {
                        long waitingTime = (cards.get(0) - game.getCardOnTable()) * 350L * ((game.getRound() + 2) / 3);
                        System.out.println(waitingTime/1000 + "s");
                        Thread.sleep(waitingTime);
                        game.playCard(username, cards.get(0));
                    } catch (Exception ignored) {}
                }
            }
    }
}