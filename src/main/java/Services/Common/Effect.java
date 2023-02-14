package Services.Common;

public class Effect {
    private String decoded;

    public Effect(int hashed) { 
        this.decoded = "0".repeat(5 - Integer.toBinaryString(hashed).length()) + Integer.toBinaryString(hashed);
    }

    public boolean isAftBurn() {
        return Character.getNumericValue(this.decoded.charAt(4)) != 0;
    }

    public boolean isAstField() {
        return Character.getNumericValue(this.decoded.charAt(3)) != 0;
    }

    public boolean isGasCld() {
        return Character.getNumericValue(this.decoded.charAt(2)) != 0;
    }

    public boolean isSupFood() {
        return Character.getNumericValue(this.decoded.charAt(1)) != 0;
    }

    public boolean isShield() {
        return Character.getNumericValue(this.decoded.charAt(0)) != 0;
    }
}