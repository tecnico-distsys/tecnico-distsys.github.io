package ttt.cli;

import java.util.*;
import javax.xml.ws.*;
import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import ttt.*; // classes generated from WSDL

public class Game {

    public static void main(String[] args) throws Exception {
        // create web service client
        // ...

        BindingProvider bindingProvider = (BindingProvider) port;
        Map<String, Object> requestContext = bindingProvider.getRequestContext();

        if (args.length < 1) {
            // get endpoint address
            Object url = requestContext.get(ENDPOINT_ADDRESS_PROPERTY);
            System.out.printf("Contacting server at %s%n", url);
        } else {
            // set endpoint address
            String url = args[0];
            requestContext.put(ENDPOINT_ADDRESS_PROPERTY, url);
            System.out.printf("Contacting server at %s%n", url);
        }

        // Start game
        Game g = new Game(port);
        g.playGame();
        g.congratulate();
    }

    private TTT ttt;
    private Scanner keyboardSc;
    private int winner = 0;
    private int player = 1;

    public Game(TTT port) {
        this.ttt = port;
        keyboardSc = new Scanner(System.in);
    }

    public int readPlay() {
        int play;
        do {
            System.out.printf("\nPlayer %d, please enter the number of the square "
                            + "where you want to place your %c (or 0 to refresh the board): %n",
                            player, (player == 1) ? 'X' : 'O');
            play = keyboardSc.nextInt();
        } while (play > 9 || play < 0);
        return play;
    }

    public void playGame() {
        int play;
        boolean playAccepted;

        do {
            player = ++player % 2;
            do {
                System.out.println(ttt.currentBoard());
                play = readPlay();
                if (play != 0) {
                    playAccepted = ttt.play( --play / 3, play % 3, player);
                    if (!playAccepted)
                        System.out.println("Invalid play! Try again.");
                } else {
                    playAccepted = false;
                }
            } while (!playAccepted);
            winner = ttt.checkWinner();
        } while (winner == -1);
    }

    public void congratulate() {
        if (winner == 2)
            System.out.printf("\nHow boring, it is a draw\n");
        else
            System.out.printf(
                    "\nCongratulations, player %d, YOU ARE THE WINNER!\n",
                    winner);
    }

}
