package example.ws.impl;

import java.io.IOException;
import javax.jws.*;
import javax.xml.ws.Endpoint;

import calc.*; // classes generated from WSDL

@WebService(
endpointInterface="calc.CalcPortType",
wsdlLocation="Calc.wsdl",
name="Calc",
portName="CalcPort",
targetNamespace="urn:calc",
serviceName="CalcService"
)
public class CalcImpl implements CalcPortType {

    // CalcPortType implementation

    public int sum(int a, int b) {
        return a+b;
    }

    public int sub(int a, int b) {
        return a-b;
    }

    public int mult(int a, int b) {
        return a*b;
    }

    public int intdiv(int a, int b) throws DivideByZero {
        if (b == 0)
        throw new DivideByZero("Cannot divide by zero!", new DivideByZeroType());
        return a/b;
    }


    // Endpoint management

    private String wsURL = null;
    private Endpoint endpoint = null;

    /** output option **/
    private boolean verbose = false;

    public boolean isVerbose() {
        return verbose;
    }
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }


    public CalcImpl(String wsURL) {
        if (wsURL == null)
            throw new NullPointerException("Web Service URL cannot be null!");
        this.wsURL = wsURL;
    }

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
