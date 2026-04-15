import java.util.ArrayList;
import java.util.List;

class ApplianceManager {

    private List<Appliance> appliances;

    public ApplianceManager() {
        appliances = new ArrayList<>();
    }

    public void addAppliance(Appliance a) {
        appliances.add(a);
    }

    public boolean removeAppliance(String name) {
        for (int i = 0; i < appliances.size(); i++) {
            if (appliances.get(i).getName().equalsIgnoreCase(name)) {
                appliances.remove(i);
                return true;
            }
        }
        return false;
    }
    public List<Appliance> getAllAppliances() {
        return appliances;
    }

    public List<Appliance> findByCategory(String category) {
        List<Appliance> result = new ArrayList<>();

        for (int i = 0; i < appliances.size(); i++) {
            if (appliances.get(i).getCategory().equalsIgnoreCase(category)) {
                result.add(appliances.get(i));
            }
        }

        return result;
    }

    public Appliance getMostConsuming() {
        if (appliances.isEmpty()) return null;

        Appliance max = appliances.get(0);

        for (int i = 1; i < appliances.size(); i++) {
            if (appliances.get(i).getMonthlyEnergy() > max.getMonthlyEnergy()) {
                max = appliances.get(i);
            }
        }
        return max;
    }


    public double getTotalMonthlyConsumption() {
        double sum = 0;

        for (int i = 0; i < appliances.size(); i++) {
            sum += appliances.get(i).getMonthlyEnergy();
        }

        return sum;
    }
}
