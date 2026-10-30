//*B. DeviceSetupRequest
//Represents setup or configuration of one or more devices.
//Required additional data:
//• int deviceCount (must be positive)
//Resolution rule:
//• awarded service points = basePoints * deviceCount

package itsupport;

public class NetworkIssueRequest extends AbstractSupportRequest {
    private final int affectedUsers;
    public NetworkIssueRequest(int id, String title, int basePoints, int affectedUsers) {
        super(id, title, basePoints);
        if (affectedUsers <= 0) {
            throw new IllegalArgumentException("Affected users must be a positive integer");
        }
        this.affectedUsers = affectedUsers;
    }

    @Override
    protected int calculatePoints() {
        //Linear rule
        return getBasePoints() + affectedUsers;
    }

    @Override
    public String toString() {
        return "(Type) NetworkIssue | (ID) " + getId() + " | (Title) " + getTitle()
                + " | (Base Points) " + getBasePoints() + " | (Resolved?) " + (isResolved() ? "Yes" : "No")
                + " | (Affected Users) " + affectedUsers;
    }
}