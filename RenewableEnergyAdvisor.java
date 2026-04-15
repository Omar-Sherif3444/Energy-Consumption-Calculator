import java.util.Arrays;
import java.util.List;

public class RenewableEnergyAdvisor extends En {
    double windKwhPerMonth;
    double solarKwhPerMonth;
    double totalRenewable;

    RenewableEnergyAdvisor(double windKwhPerMonth,double solarKwhPerMonth){
        super(solarKwhPerMonth,windKwhPerMonth);

        this.solarKwhPerMonth=solarKwhPerMonth;
        this.windKwhPerMonth=windKwhPerMonth;
    }

    public double getTotalRenewableEnergy() {
        return totalRenewable=windKwhPerMonth+solarKwhPerMonth;
    }
    public double getRenewablePercentage  (List<Appliance> appliances) {
        double totalConsumption = calculateTotalConsumption(appliances);
        double renewable = solarKwhPerMonth + windKwhPerMonth;

        if (totalConsumption == 0) {
            return 0;
        }

        return (renewable / totalConsumption) * 100.0;
    }
    public double estimateMonthlySavings(double kiloWattPrice) {
        return totalRenewable*kiloWattPrice;

    }
    public List<String> getRenewableTips() {
        return Arrays.asList(
                "Install rooftop solar panels to reduce electricity costs.",
                "Improve home insulation to lower heating and cooling energy.",
                "Use high-efficiency LED lighting.",
                "Upgrade to Energy Star-certified appliances.",
                "Use a smart thermostat to optimize energy usage.",
                "Consider installing a heat pump for efficient heating/cooling."
        );
    }

}


