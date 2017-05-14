package examples.RMIShape;

import java.rmi.*;
import java.rmi.registry.*;

public class ShapeListServer {
    public static void main(String args[]){
        int registryPort = 1099;
        System.out.println("Main OK");
        try{
            ShapeList aShapelist = new ShapeListServant();
            System.out.println("After create");

            Registry reg = LocateRegistry.createRegistry(registryPort);
            reg.rebind("ShapeList", aShapelist);

            // A more realistic would be having an autonomous RMI Registry
            // available at the default port
            // (implies defining a 'codebase' to allow the RMI Registry
            // to remotely obtain the interfaces for the
            // objects that will be registered):
            //
            // Naming.rebind("ShapeList", aShapelist);

            System.out.println("ShapeList server ready");

            System.out.println("Awaiting connections");
            System.out.println("Press enter to shutdown");
            System.in.read();
            System.exit(0);

        }catch(Exception e) {
            System.out.println("ShapeList server main " + e.getMessage());
        }
    }
}
