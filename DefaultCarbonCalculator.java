public class DefaultCarbonCalculator implements CarbonCalculator {

    private double emissionFactor;

    public DefaultCarbonCalculator(double emissionFactor) {
        this.emissionFactor = emissionFactor;
    }

    @Override
    public double calculateCO2(double kWh) {
        return kWh * emissionFactor;
    }

    public void setEmissionFactor(double f) {
        this.emissionFactor = f;
    }
}
