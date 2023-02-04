package Enums;

public enum EffectFlag {
    AFTERBURNER(1),
    ASTEROID_FIELD(2),
    GAS_CLOUD(4),
    SUPERFOOD(8),
    SHIELD(16);

    public final Integer value;

    private EffectFlag(Integer value) {
      this.value = value;
    }
}