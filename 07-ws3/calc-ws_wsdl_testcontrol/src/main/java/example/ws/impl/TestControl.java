package example.ws.impl;

import java.io.IOException;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.xml.ws.Endpoint;


@WebService(targetNamespace = "urn:pt:ulisboa:tecnico:ws:test")
public class TestControl {

    // test control implementation

    @WebMethod
    public void reset() {
        System.out.println("Resetting data for tests...");
        // invoke a method to reset calc state...
    }

    @WebMethod
    public void terminate() {
        System.out.println("Received shutdown command. Shutting the server down...");
        System.exit(0);
    }


    // Endpoint management

    private String wsURL = null;
    private Endpoint endpoint = null;

    /** output option **/
    private boolean verbose = false;

    @WebMethod(exclude=true)
    public boolean isVerbose() {
        return verbose;
    }
    @WebMethod(exclude=true)
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }


    public TestControl(String wsURL) {
        if (wsURL == null)
            throw new NullPointerException("Web Service URL cannot be null!");
        this.wsURL = wsURL;
    }

    @WebMethod(exclude=true)
    public void start() {
        try {
            // publish endpoint
            endpoint = Endpoint.create(this);
            if (verbose)
                System.out.printf("Starting %s%n", wsURL);
            endpoint.publish(wsURL);

        } catch(Exception e) {
            if (verbose) {
                System.out.printf("Caught exception when starting: %s%n", e);
                e.printStackTrace();
            }

        }
    }

    @WebMethod(exclude=true)
    public void awaitConnections() {
        if (verbose) {
            System.out.println("Awaiting connections");
            System.out.println("Press enter to shutdown");
        }
        try {
            System.in.read();
        } catch(IOException e) {
            if (verbose)
                System.out.printf("Caught i/o exception when awaiting requests: %s%n", e);
        }
    }

    @WebMethod(exclude=true)
    public void stop() {
        try {
            if (endpoint != null) {
                // stop endpoint
                endpoint.stop();
                if (verbose)
                    System.out.printf("Stopped %s%n", wsURL);
            }
        } catch(Exception e) {
            if (verbose)
                System.out.printf("Caught exception when stopping: %s%n", e);
        }
    }

}
