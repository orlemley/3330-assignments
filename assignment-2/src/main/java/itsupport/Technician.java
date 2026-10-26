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
    private final String NAME;
    private int servicePoints;

    public Technician(String name) {
        if(name==null || name.isEmpty()){
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.NAME = name;
        this.servicePoints = 0;
    }

    public String getNAME() {
        return NAME;
    }

    public int getServicePoints() {
        return this.servicePoints;
    }

    public void addPoints(int points) {
        if(this.servicePoints + points > 0){
            this.servicePoints += points;
        }else{
            this.servicePoints = 0;
            System.out.println("Added points cannot lead to a total less than zero, service points have been set to 0.");
        }
    }

    @Override
    public String toString() {
        return "Technician [name=" + NAME + ", points=" + servicePoints + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.NAME);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj.getClass() == Technician.class){
            Technician other = (Technician) obj;
            return this.NAME.equals(other.NAME);
        }
        return false;
    }






}