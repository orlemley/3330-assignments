//*B. DeviceSetupRequest
//Represents setup or configuration of one or more devices.
//Required additional data:
//• int deviceCount (must be positive)
//Resolution rule:
//• awarded service points = basePoints * deviceCount

package itsupport;

public class NetworkIssueRequest extends AbstractSupportRequest {
    public int affectedUsers;
    public NetworkIssueRequest(int affectedUsers, String title, int id) {
        super(id, title, affectedUsers);
        if (affectedUsers <= 0) {
            throw new IllegalArgumentException("Affected users must be a positive integer");
        }
        this.affectedUsers = affectedUsers;

        //Linear rule
        this.awardedPoints = this.getBasePoints() + affectedUsers;
    }

    @Override
    public String toString() {
        return "NetworkIssueRequest [affected users=" + this.affectedUsers + "]" + super.toString();
    }
}