package Enums;

public enum StateTypes {
    DEFAULT_STATE(1),
    ATTACK_STATE(2),
    ESCAPE_STATE(3),
    DODGE_STATE(4);

    public final Integer value;

    private StateTypes(Integer value) {
      this.value = value;
    }
}
