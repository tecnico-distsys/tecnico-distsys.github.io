package examples.RMIShape;

import java.rmi.*;
import java.rmi.server.*;
import java.util.Vector;
import java.awt.Rectangle;
import java.awt.Color;

public class ShapeListClient {
    public static void main(String args[]) {
        String option = "Read";
        String shapeType = "Rectangle";
        if (args.length > 0)
            option = args[0]; // read or write
        if (args.length > 1)
            shapeType = args[1]; // specify Circle, Line etc
        System.out.println("option = " + option + "shape = " + shapeType);
        ShapeList aShapeList = null;
        try {
            aShapeList = (ShapeList) Naming.lookup("//Jean.torriano.net/ShapeList");
            System.out.println("Found server");
            Vector sList = aShapeList.allShapes();
            System.out.println("Got vector");
            if (option.equals("Read")) {
                for (int i = 0; i < sList.size(); i++) {
                    GraphicalObject g = ((Shape) sList.elementAt(i)).getAllState();
                    g.print();
                }
            } else {
                GraphicalObject g = new GraphicalObject(shapeType, new Rectangle(50, 50, 300, 400), Color.red,
                        Color.blue, false);
                System.out.println("Created graphical object");
                aShapeList.newShape(g);
                System.out.println("Stored shape");
            }
        } catch (RemoteException e) {
            System.out.println("allShapes: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Lookup: " + e.getMessage());
        }
    }
}
