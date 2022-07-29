package model;

import server.Game;

import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public class Bot extends Thread{
    Vector<Integer> cards;
    Game game;
    int num;
    final AtomicBoolean keepPlaying = new AtomicBoolean(true);

    public Bot(int num) {
       this.num = num;
       cards = new Vector<>();
    }


    public Vector<Integer> getCards() {
        return cards;
    }

    public AtomicBoolean getKeepPlaying() {
        return keepPlaying;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void play() throws InterruptedException {
        waiting();
        if (getCards().size() != 0) {
            Integer card = getCards().get(0);
            game.setCardOnTable(card);
            getCards().remove(card);

            System.out.println(game.getAllPlayingCards().toString());

            for (ClientHandler c : game.clientHandlers) {
                c.sendMessage("Bot" + this.num + " played " + card);
            }
        }
    }

    public void waiting() throws InterruptedException {
        if (getCards().size() != 0) {
            Integer last = game.getCardOnTable();
            float time = ((float)((getCards().get(0) - last))/3f) * 1000;
            System.out.println(time/1000 + "s");
            Thread.sleep((long) time);

            //******
            if (last != game.getCardOnTable()) {
                Thread.interrupted();
                waiting();
            }
            //******

        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (getCards().size() != 0)
                play();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
