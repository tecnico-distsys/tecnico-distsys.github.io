package example.ws.impl;

import javax.xml.ws.Endpoint;

public class CalcMain {

    public static void main(String[] args) throws Exception {
        // Check arguments
        if (args.length < 1) {
            System.err.println("Argument(s) missing!");
            System.err.printf("Usage: java %s url%n", CalcMain.class.getName());
            return;
        }
        final String URL = args[0];

        CalcImpl impl = new CalcImpl(URL);
        impl.setVerbose(true);
        impl.start();
        TestControl testControl = null;
        if ("true".equalsIgnoreCase(System.getProperty("ws.test"))) {
            testControl = new TestControl(URL + "/test");
            testControl.setVerbose(true);
            testControl.start();
        }

        impl.awaitConnections();

        impl.stop();
        if (testControl != null) {
            testControl.stop();
        }
    }

}
