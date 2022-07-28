package server;

import model.Bot;
import model.ClientHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Game extends Thread{
    Vector<ClientHandler> clientHandlers;
    Vector<Bot> bots;

    public static AtomicBoolean startBots;

    public static int cardOnTable;
    int hearts;
    int ninjas;
    Vector<Integer> deckOfAll;
    Vector<Integer> allPlayingCards;

    public Game(Vector<ClientHandler> clientHandlers, Vector<Bot> bots) {
        this.clientHandlers = clientHandlers;
        this.bots = bots;
        deckOfAll = new Vector<>();
        for (int i = 1; i < 101; i++) {
            deckOfAll.add(i);
        }
        ninjas = 2;
        hearts = clientHandlers.size() + bots.size();
    }

    public Vector<Integer> getAllPlayingCards() {
        return allPlayingCards;
    }

    public void setAllPlayingCards(Vector<Integer> allPlayingCards) {
        this.allPlayingCards = allPlayingCards;
    }

    public int getCardOnTable() {
        return cardOnTable;
    }

    public void setCardOnTable(int cardOnTable) {
        Game.cardOnTable = cardOnTable;
    }

    public static AtomicBoolean getStartBots() {
        return startBots;
    }

    public void startRound(int round) {

        //*** guess this one method works fine completely (doesn't need much debugging) =)

        setCardOnTable(0);
        allPlayingCards = new Vector<>();

        if (round == 2 || round == 5 || round == 8) {
            ninjas++;
        }
        if (round == 3 || round == 6 || round == 9) {
            hearts++;
        }

        Collections.shuffle(deckOfAll);
        int k = 0;
        for (ClientHandler c : clientHandlers) {
            for (int j = k; j < round + k; j++) {
                c.getCards().add(deckOfAll.get(j));
                allPlayingCards.add(deckOfAll.get(j));
            }
            k += round;
            Collections.sort(c.getCards());
            update(c, round);
        }
        for (Bot b : bots) {
            for (int j = k; j < round + k; j++) {
                b.getCards().add(deckOfAll.get(j));
                allPlayingCards.add(deckOfAll.get(j));
            }
            k += round;
            Collections.sort(b.getCards());
        }

        Collections.sort(allPlayingCards);

        for (Bot b : bots) {
            b.start();

            //*** printing to check if bots cards work
            System.out.println("bot: " + b.getCards().toString());
        }


        System.out.println("allPlayingCards: " + allPlayingCards);
    }


    public void update(ClientHandler c, int round) {
        c.sendMessage( "Round: " + round + "\n" +
                           "Hearts: " + hearts + "\n" +
                           "Ninjas: " + ninjas + "\n" +
                           "Your Cards: " + c.getCards().toString()+ "\n" +
                           "Last Played Card = " + cardOnTable);
    }


    @Override
    public void run() {

        //*** needs a lot of debugging :)))


        for (int i = 1; i < 12; i++) {
             startRound(i);

            AtomicBoolean roundIsFinished = new AtomicBoolean(false);

             Integer last = getCardOnTable();

             while (!roundIsFinished.get()) {
                 if (last != getCardOnTable()) {
                   last = getCardOnTable();

                   if (last.equals(getAllPlayingCards().get(0))) {

                       if (getAllPlayingCards().size() == 1) {
                           for (ClientHandler c : clientHandlers) {
                               c.sendMessage("You passed this round!");
                           }
                           roundIsFinished.set(true);
                       }
                       else {
                           getAllPlayingCards().remove(last);
                           for (ClientHandler c : clientHandlers) {
                               update(c, i);
                           }
                       }
                   }
                   else {

                       hearts--;

                       ArrayList<Integer> remover = new ArrayList<>();

                       for (int x = 0; x < getAllPlayingCards().indexOf(getCardOnTable()); x++) {
                           for (ClientHandler c : clientHandlers) {
                               c.getCards().remove(getAllPlayingCards().get(x));
                           }
                           for (Bot b : bots) {
                               b.getCards().remove(getAllPlayingCards().get(x));
                           }
                           remover.add(getAllPlayingCards().get(x));
                       }
                        for (Integer n : remover) {
                            getAllPlayingCards().remove(n);
                        }

                       if (hearts == 0) {
                           for (ClientHandler c : clientHandlers) {
                               c.sendMessage("You failed this round!");
                        }
                     }
                       else {
                           for (ClientHandler c : clientHandlers) {
                               c.getGame().update(c, ++i);
                           }
                       }
                  }
               }
            }
        }
    }
}
