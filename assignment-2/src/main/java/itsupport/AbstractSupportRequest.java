/**2) AbstractSupportRequest (abstract class)
Create an abstract base class that implements the shared parts of SupportRequest.
Required data:
        • int id
• String title
• int basePoints
• boolean resolved
Required invariants:
        • id > 0
        • title is not null or blank
• basePoints > 0
Required behavior:
        • Constructor validates invariants and throws IllegalArgumentException for invalid input
• Common getters
• Shared resolved-state handling
• A useful toString()
A request should not be resolved more than once. A repeated resolution attempt should fail
consistently, preferably by throwing IllegalStateException.*/

package itsupport;

public abstract class AbstractSupportRequest implements SupportRequest {
    private final int id;
    private String title;
    private int basePoints = 1;
    private final int multiplier;
    protected int awardedPoints;
    protected boolean resolved;

    public AbstractSupportRequest(int id, String title, int multiplier) {
        if (id <= 0) {
            throw new IllegalArgumentException("Support request id must be greater than 0");
        }

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Support request title cannot be null or blank");
        }

        if (multiplier <= 0) {
            throw new IllegalArgumentException("Support request multiplier must be an int greater than 0");
        }

        this.id = id;
        this.title = title;
        this.multiplier = multiplier;
        this.resolved = false;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public synchronized String getTitle() {
        return title;
    }

    @Override
    public synchronized int getBasePoints() {
        return basePoints;
    }

    @Override
    public synchronized boolean isResolved() {
        return resolved;
    }

    public synchronized boolean setTitle(String newTitle) {
        if (resolved) {
            throw new IllegalStateException("Cannot change the title of support request '" + getId() + "' because it has already been resolved");
        }

        if (newTitle == null || newTitle.isBlank()) {
            throw new IllegalArgumentException("Support request title cannot be null or blank");
        }

        title = newTitle;
        return true;
    }

    // These two must remain synchronized since they read basePoints, but synchronized allows reentry,
    // so it's not a problem at all!
    public synchronized boolean upgradeBy(int points) {
        try {
            return setBasePoints(Math.addExact(basePoints, points));
        } catch (ArithmeticException arithmeticException) {
            if (resolved) {
                throw new IllegalStateException("Cannot modify the base points of support request '" + getId() + "' because it has already been resolved");
            } else {
                throw arithmeticException;
            }
        }
    }

    public synchronized boolean downgradeBy(int points) {
        try {
            return setBasePoints(Math.subtractExact(basePoints, points));
        } catch (ArithmeticException arithmeticException) {
            if (resolved) {
                throw new IllegalStateException("Cannot modify the base points of support request '" + getId() + "' because it has already been resolved");
            } else {
                throw arithmeticException;
            }
        }
    }

    public synchronized boolean setBasePoints(int points) {
        if (resolved) {
            throw new IllegalStateException("Cannot modify the base points of support request '" + getId() + "' because it has already been resolved");
        }

        if (points <= 0) {
            throw new IllegalArgumentException("Base points must be greater than 0");
        }

        basePoints = points;
        return true;
    }

    protected synchronized int resolve() {
        if (resolved) {
            throw new IllegalStateException("Cannot resolve support request '" + getId() + "' because it has already been resolved");
        }
        this.resolved = true;
        return this.awardedPoints;
    }

    public synchronized int resolveFor(Technician technician) {
        this.resolve();
        technician.addPoints(this.awardedPoints);
        return this.awardedPoints;
    }

    // Default toString()
    @Override
    public String toString() {
        return "BaseRequest [id=" + this.id + " title=" + this.title + ", basePoints=" + this.basePoints + ", multiplier=" + this.multiplier + "resolved=" + this.resolved + "]";
    }
}