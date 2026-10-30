/**4) Technician
Represents an IT technician who can be assigned support requests.
Required data:
        • String name (not null or blank)
• int servicePoints (starts at 0 and must never become negative)
Required behavior:
        • Constructor validates the technician name
5
        • addPoints(int amount) with fail-fast validation
• getServicePoints()
• toString() includes the technician name and service points
Required equals and hashCode: Technicians will be stored in a HashSet<Technician>.
Implement equals and hashCode so that two Technician objects are considered equal when their
name values match exactly.
Your equals and hashCode implementations must be consistent with each other.*/

package itsupport;

import java.util.Objects;

public class Technician {
    private final String name;
    private int servicePoints;

    public Technician(String name) {
        if(name==null || name.isBlank()){
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.name = name;
        this.servicePoints = 0;
    }

    public String getName() {
        return name;
    }

    public int getServicePoints() {
        return this.servicePoints;
    }

    public void addPoints(int points) {
        if (points < 0 && servicePoints + points < 0) {
            throw new IllegalStateException("Cannot subtract " + -points + " points from technician '" + name + "' because the technician only has " + servicePoints + " points, and a technician's points cannot go below 0");
        }

        servicePoints += points;
    }

    @Override
    public String toString() {
        return "Technician [name=" + name + ", points=" + servicePoints + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Technician other){
            return this.name.equals(other.name);
        }
        return false;
    }






}