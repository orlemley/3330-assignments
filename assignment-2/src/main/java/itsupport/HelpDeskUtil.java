/**6) HelpDeskUtil (generic utilities)
Create a small utility class to practice generics safely.
Requirement A: Generic method
• Implement public static <T> void printAll(List<T> items)
• It prints each item on its own line using toString().
Requirement B: Wildcard method
• Implement public static int sumPoints(List<? extends Technician> technicians)
The method returns the total service points across all technicians in the list.
The upper-bounded wildcard allows the method to work with Technician objects or any future
subclass of Technician, while safely reading them as Technician. You are not required to create
any technician subclasses for this assignment.*/

package itsupport;

import java.util.List;

// I must not understand the method because generics don't seem to provide any benefit if we're just calling toString()
// So I should revisit this requirement
public final class HelpDeskUtil {
    private HelpDeskUtil() {}

    public static <T> void printAll(List<T> items) {
        if (items == null) {
            return;
        }

        for (T item : items) {
            // toString() implicit in println, but I included it explicitly too
            System.out.println(item.toString());
        }
    }

    public static int sumPoints (List<? extends Technician> technicians) {
        int totalServicePoints = 0;

        for (Technician technician : technicians) {
            totalServicePoints += technician.getServicePoints();
        }

        return totalServicePoints;
    }
}