package antifraud.entity.enums;

public enum Role {
    ADMINISTRATOR(10), SUPPORT(7), MERCHANT(3);

    private Integer priority;

    private Role(Integer priority) {
        this.priority = priority;
    }

    public Integer getPriority() {
        return priority;
    }
}
