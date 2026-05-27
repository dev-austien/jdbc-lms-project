public enum PricingUnit {
    PER_KG,
    PER_PIECE;

    public static PricingUnit fromDb(String value) {
        return PricingUnit.valueOf(value);
    }

    public String label() {
        return switch (this) {
            case PER_KG -> "per 8 kg";
            case PER_PIECE -> "per piece";
        };
    }

    public String quantityPrompt() {
        return switch (this) {
            case PER_KG -> "Enter weight (kg): ";
            case PER_PIECE -> "Enter number of pieces: ";
        };
    }
}
