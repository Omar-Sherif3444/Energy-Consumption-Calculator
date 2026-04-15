import java.io.Serializable;

public abstract class Appliance implements Serializable{
    private String name;
    private double powerUsage;
    private double dailyUsageHours;
    private String category;
    private boolean canBeTurnedOff;

    public Appliance(String name, double powerUsage,
                     double dailyUsageHours, String category,
                     boolean canBeTurnedOff) {
        this.name = name;
        this.powerUsage = powerUsage;
        this.dailyUsageHours = dailyUsageHours;
        this.category = category;
        this.canBeTurnedOff = canBeTurnedOff;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPowerUsage(double powerUsage) {
        this.powerUsage = powerUsage;
    }

    public double getPowerUsage() {
        return powerUsage;
    }

    public void setDailyUsageHours(double dailyUsageHours) {
        this.dailyUsageHours = dailyUsageHours;
    }

    public double getDailyUsageHours() {
        return dailyUsageHours;
    }

    public String getCategory() {
        return category;
    }

    public boolean isTurnOffPossible() {
        return canBeTurnedOff;
    }

    public boolean validateValues() {
        return powerUsage > 0 && dailyUsageHours > 0;
    }

    public double getMonthlyEnergy() {
        return calculateMonthlyEnergy();
    }

    @Override
    public String toString() {
        return "Appliance Name: " + name +
                "\nPower Usage: " + powerUsage + " W" +
                "\nDaily Usage: " + dailyUsageHours + " hours" +
                "\nCategory: " + category +
                "\nCan Be Turned Off: " + (canBeTurnedOff ? "Yes" : "No");
    }

    public abstract double calculateDailyEnergy();
    public abstract double calculateMonthlyEnergy();
}
