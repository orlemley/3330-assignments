/**A. PasswordResetRequest
Represents a password or account reset request.
Required additional data:
        • String accountName (not null or blank)
Resolution rule:
        • awards exactly basePoints*/

package itsupport;

public class PasswordResetRequest extends AbstractSupportRequest {

    private final String accountName;
    public PasswordResetRequest(int id, String title, int basePoints, String name) {
        super(id, title, basePoints);
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.accountName = name;
    }

    @Override
    protected int calculatePoints() {
        return getBasePoints();
    }

    @Override
    public String toString() {
        return "PasswordResetRequest [accountName=" + accountName + "] " + super.toString();
    }
}