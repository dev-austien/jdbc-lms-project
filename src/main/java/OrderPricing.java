public final class OrderPricing {

    private static final double KG_PER_UNIT = 8.0;

    public static double calculateLineTotal(double quantity, double unitPrice, PricingUnit pricingUnit) {
        return switch (pricingUnit) {
            case PER_KG -> Math.ceil(quantity / KG_PER_UNIT) * unitPrice;
            case PER_PIECE -> quantity * unitPrice;
        };
    }
}
