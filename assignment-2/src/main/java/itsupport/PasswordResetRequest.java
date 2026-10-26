/**A. PasswordResetRequest
Represents a password or account reset request.
Required additional data:
        • String accountName (not null or blank)
Resolution rule:
        • awards exactly basePoints*/

package itsupport;

public class PasswordResetRequest extends AbstractSupportRequest {

    String accountName;
    public PasswordResetRequest(String title, String name, int id) {
        super(id, title, 1);
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.accountName = name;
        this.awardedPoints = this.getBasePoints();

    }


    @Override
    public String toString() {
        return "PasswordResetRequest [accountName=" + accountName + "] " + super.toString();
    }
}