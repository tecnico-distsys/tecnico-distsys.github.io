package example.ws.cli;

import calc.DivideByZero;


/**
 *  Example program that calls remote operations.
 */
public class CalcClientMain {

    public static void main(String[] args) throws Exception {

        CalcClient client = new CalcClient();

        System.out.print("1 + 2 = ");
        System.out.println(client.sum(1, 2));

        System.out.print("22 - 2 = ");
        System.out.println(client.sub(22, 2));

        System.out.print("7 * 8 = ");
        System.out.println(client.mult(7, 8));

        System.out.print("10 / 4 = ");
        System.out.println(client.intdiv(10, 4));

        try {
            System.out.print("13 / 0 = ");
            System.out.println(client.intdiv(13, 0));

        } catch(DivideByZero e) {
            System.out.println("Caught expected divide by zero exception.");
        }

    }

}
