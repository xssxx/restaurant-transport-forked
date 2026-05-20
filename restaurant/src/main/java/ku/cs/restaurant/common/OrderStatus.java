package ku.cs.restaurant.common;

public enum OrderStatus {
    PENDING,
    COOKING,
    READY,
    DELIVERING,
    DELIVERED,
    SUCCESS,
    COMPLETE,
    CANCEL;

    public boolean canTransitionTo(OrderStatus next) {
        return switch (this) {
            case PENDING    -> next == COOKING  || next == CANCEL;
            case COOKING    -> next == READY    || next == CANCEL;
            case READY      -> next == DELIVERING;
            case DELIVERING -> next == DELIVERED;
            case DELIVERED  -> next == SUCCESS;
            case SUCCESS    -> next == COMPLETE;
            case COMPLETE, CANCEL -> false; // terminal states
        };
    }
}
