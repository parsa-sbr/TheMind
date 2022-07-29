package server;

import model.Bot;
import model.ClientHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public class Game extends Thread{
    public Vector<ClientHandler> clientHandlers;
    Vector<Bot> bots;
    public static int cardOnTable;
    int hearts;
    int ninjas;
    Vector<Integer> deckOfAll;
    Vector<Integer> allPlayingCards;
    AtomicBoolean gameIsAlive = new AtomicBoolean(true);
    AtomicBoolean ninjaWasPlayed = new AtomicBoolean(false);

    public Game(Vector<ClientHandler> clientHandlers, Vector<Bot> bots) {
        this.clientHandlers = clientHandlers;
        this.bots = bots;
        deckOfAll = new Vector<>();
        for (int i = 1; i < 101; i++) {
            deckOfAll.add(i);
        }
        ninjas = 2;
        //hearts = clientHandlers.size() + bots.size();
        hearts = 1000;
    }

    public Vector<Integer> getAllPlayingCards() {
        return allPlayingCards;
    }

    public int getCardOnTable() {
        return cardOnTable;
    }

    public void setCardOnTable(int cardOnTable) {
        Game.cardOnTable = cardOnTable;
    }

    public AtomicBoolean getGameIsAlive() {
        return gameIsAlive;
    }

    public int getNinjas() {
        return ninjas;
    }

    public void startRound(int round) {
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

        for (Bot b : bots) {
            for (int j = k; j < round + k; j++) {
                b.getCards().add(deckOfAll.get(j));
                allPlayingCards.add(deckOfAll.get(j));
            }
            k += round;
            Collections.sort(b.getCards());
        }
        for (ClientHandler c : clientHandlers) {
            for (int j = k; j < round + k; j++) {
                c.getCards().add(deckOfAll.get(j));
                allPlayingCards.add(deckOfAll.get(j));
            }
            k += round;
            Collections.sort(c.getCards());
            update(c, round);
        }


        Collections.sort(allPlayingCards);


        if (round == 1) {
            for (Bot b : bots) {
                b.start();
            }
        }

        System.out.println(allPlayingCards.toString());
    }


    public void update(ClientHandler c, int round) {
        String update = "";
            for (Bot b : bots) {
                update += b.getUsername() + ": " + b.getCards().size() + " ";
            }
            for (ClientHandler other : clientHandlers) {
                if (!other.getUsername().equals(c.getUsername())) {
                    update += other.getUsername() + ": " + other.getCards().size() + " ";
                }
            }

        c.sendMessage( "Round: " + round + " " + "Hearts: " + hearts + " " + "Ninjas: " + ninjas + "\n" +
                           update + "\n" +
                           "Your Cards: " + c.getCards().toString()+ "\n" +
                           "Last Played Card = " + cardOnTable);
    }


    public void sendToAll(String username, String message) {
        for (ClientHandler c : clientHandlers) {
            if (!c.getUsername().equals(username)) {
                c.sendMessage(username + ": " + message);
            }
        }
    }

    public void playCard(String username, Integer card) {

            setCardOnTable(card);


        String played = String.valueOf(card);
        if (card == -2) {
            played = "ninja";
        }
        for (ClientHandler c : clientHandlers) {
            if (c.getUsername().equals(username)) {
                c.getCards().remove(card);
            }
        }
        for (Bot b : bots) {
            if (b.getUsername().equals(username)) {
                b.getCards().remove(card);
            }
        }
        sendToAll(username, " played card " + played);
        System.out.println(allPlayingCards.toString());
    }


    public ArrayList<Integer> applyNinja() {
        --ninjas;

        ArrayList<Integer> handle = new ArrayList<>();

        for (ClientHandler c : clientHandlers) {
            if (c.getCards().size() != 0) {
                handle.add(c.getCards().get(0));
                c.getCards().remove(c.getCards().get(0));
            }
        }
        for (Bot b : bots) {
            if (b.getCards().size() != 0) {
                handle.add(b.getCards().get(0));
                b.getCards().remove(b.getCards().get(0));
            }
        }

        Collections.sort(handle);
        Collections.reverse(handle);

      //  setCardOnTable(handle.get(handle.size() - 1));

        for (Integer n : handle) {
            allPlayingCards.remove(n);
        }
        return handle;
    }




    @Override
    public void run() {

        while (getGameIsAlive().get()) {
           loop: for (int i = 1; i < 13; i++) {
                startRound(i);
                for (Bot b : bots) {
                    b.getPlay().set(true);
                }

                AtomicBoolean roundIsFinished = new AtomicBoolean(false);

                Integer last = getCardOnTable();

                while (!roundIsFinished.get() && gameIsAlive.get()) {
                    if (last != getCardOnTable()) {

                        //*****
                        for (Bot b : bots) {
                            b.getPlay().set(false);
                            b.interrupt();
                        }
                        //*****

                        if (getCardOnTable() == -2) {
                            ninjaWasPlayed.set(true);
                            setCardOnTable(applyNinja().get(0));
//                            last = getCardOnTable();
//
//                            ArrayList<Integer> handle = new ArrayList<>();
//                            for (ClientHandler c : clientHandlers) {
//                                handle.addAll(c.getCards());
//                            }
//                            allPlayingCards.removeIf(n -> !handle.contains(n));

                            if (allPlayingCards.size() == 0) {
                                for (ClientHandler c : clientHandlers) {
                                    update(c, i);

                                    if (i == 12) {
                                        c.sendMessage("You won!");
                                    }
                                    else {
                                        c.sendMessage("You passed this round!");
                                    }
                                }
                                roundIsFinished.set(true);
                            }
                            else {
                                for (ClientHandler c : clientHandlers) {
                                    update(c, i);
                                }
                            }
                        }




                        else {
                            last = getCardOnTable();



                            if (last.equals(getAllPlayingCards().get(0))) {   /* اگه سر جاش بازی کرده */

                                if (getAllPlayingCards().size() == 1) {
                                    if (i == 12) {
                                        for (ClientHandler c : clientHandlers) {
                                            update(c, i);
                                            c.sendMessage("You won!");
                                        }
                                    }
                                    else {
                                        for (ClientHandler c : clientHandlers) {
                                            update(c, i);
                                            c.sendMessage("You passed this round!");
                                        }
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

                                --hearts;

                                ArrayList<Integer> remover = new ArrayList<>();

                                for (int x = 0; x < getAllPlayingCards().indexOf(getCardOnTable()) + 1; x++) {
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
                                        c.sendMessage("You lost!");
                                    }
                                    roundIsFinished.set(true);
                                    gameIsAlive.set(false);
                                    break loop;
                                }
                                else {
                                    for (ClientHandler c : clientHandlers) {
                                        update(c, i);
                                    }
                                    if (allPlayingCards.size() == 0) {
                                        if (i == 12) {
                                            for (ClientHandler c : clientHandlers) {
                                                c.sendMessage("You won!");
                                            }
                                            gameIsAlive.set(false);
                                            break loop;
                                        }
                                        else {
                                            for (ClientHandler c : clientHandlers) {
                                                c.sendMessage("You passed this round!");
                                            }
                                        }
                                        roundIsFinished.set(true);
                                    }
                                }
                            }
                        }
                        if (!ninjaWasPlayed.get()) {
                            for (Bot b : bots) {
                                b.getPlay().set(true);
                            }
                        }

                    }
                }

            }
        }
    }
}
