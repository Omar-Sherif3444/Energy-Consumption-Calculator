import java.util.List;

public class En {
    private static final int DAYS_PER_MONTH = 30;
    private ApplianceManager applianceManager;
    private CarbonCalculator carbonCalculator;
    private double kiloWattPrice;
    private double monthlyEnergy;
    private double monthlyCost;
    private double carbonFootprint;

    public En(ApplianceManager applianceManager, CarbonCalculator carbonCalculator, double kiloWattPrice) {
        this.applianceManager = applianceManager;
        this.carbonCalculator = carbonCalculator;
        this.kiloWattPrice = kiloWattPrice;
    }

    public En(double solarKwhPerMonth, double windKwhPerMonth) {
    }

    public static double calculateMonthlyConsumption(Appliance appliance) {
        if (!appliance.validateValues()) {
            throw new IllegalArgumentException("Invalid appliance values.");
        }

        return (appliance.getPowerUsage() / 1000.0) *
                appliance.getDailyUsageHours() *
                DAYS_PER_MONTH;
    }

    public static double calculateTotalConsumption(List<Appliance> appliances) {
        double monthlyConsumption = 0;

        for (Appliance a : appliances) {
            monthlyConsumption += calculateMonthlyConsumption(a);
        }
        return monthlyConsumption;
    }

    public static double calculateTotalCost(double kiloWattPrice, List<Appliance> appliances) {
        double totalCost = 0;

        for (Appliance a : appliances) {
            double applianceConsumption = calculateMonthlyConsumption(a) * kiloWattPrice;
            totalCost += applianceConsumption;
        }
        return totalCost;
    }

    public void calculateAll() {
        List<Appliance> appliances = applianceManager.getAllAppliances();

        monthlyEnergy = calculateTotalConsumption(appliances);
        monthlyCost = calculateTotalCost(kiloWattPrice, appliances);
        carbonFootprint = carbonCalculator.calculateCO2(monthlyEnergy);
    }

    public double getMonthlyEnergy() {
        return monthlyEnergy;
    }

    public double getMonthlyCost() {
        return monthlyCost;
    }

    public double getCO2() {
        return carbonFootprint;
    }
}
