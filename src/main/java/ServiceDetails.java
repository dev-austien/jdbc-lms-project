public record ServiceDetails(
        int serviceId,
        String serviceType,
        String description,
        double unitPrice,
        PricingUnit pricingUnit
) {
}
