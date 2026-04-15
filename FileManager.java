import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileManager {

    private String fileName;
    private String separator;

    public FileManager(String fileName, String separator) {
        this.fileName = fileName;
        this.separator = separator;
    }

    public void save(List<Appliance> appliances) {
        try {
            FileWriter writer = new FileWriter(fileName);
            for (Appliance a : appliances) {
                writer.write(a.getName() + separator + a.getPowerUsage() + "\n");
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving the file");
        }
    }

    public void saveMonthlyConsumption(String month, double consumption) {
        try {
            String monthFile = "monthly_" + month + ".txt";
            FileWriter writer = new FileWriter(monthFile, false);
            writer.write("Month: " + month + "\n");
            writer.write("Consumption (kWh): " + consumption + "\n");
            writer.close();
            System.out.println("? Saved consumption data for " + month);
        } catch (IOException e) {
            System.out.println("Error saving monthly consumption: " + e.getMessage());
        }
    }

    public double loadMonthlyConsumption(String month) {
        String monthFile = "monthly_" + month + ".txt";
        File f = new File(monthFile);
        
        if (!f.exists()) {
            return 0.0;
        }

        try {
            Scanner sc = new Scanner(f);
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.startsWith("Consumption (kWh):")) {
                    String[] parts = line.split(":");
                    double consumption = Double.parseDouble(parts[1].trim());
                    sc.close();
                    return consumption;
                }
            }
            sc.close();
        } catch (Exception e) {
            System.out.println("Error loading monthly consumption: " + e.getMessage());
        }
        return 0.0;
    }

    public double calculateAnnualConsumption() {
        String[] months = {"January", "February", "March", "April", "May", "June",
                          "July", "August", "September", "October", "November", "December"};
        
        double annualTotal = 0.0;
        System.out.println("\n=== ANNUAL CONSUMPTION REPORT ===");
        
        for (String month : months) {
            double monthlyConsumption = loadMonthlyConsumption(month);
            if (monthlyConsumption > 0) {
                System.out.printf("%s: %.2f kWh%n", month, monthlyConsumption);
                annualTotal += monthlyConsumption;
            }
        }
        
        System.out.printf("\nTotal Annual Consumption: %.2f kWh%n", annualTotal);
        return annualTotal;
    }

    public void saveAllMonthlyConsumptions(Map<String, Double> monthlyData) {
        try {
            String summaryFile = "annual_consumption_summary.txt";
            FileWriter writer = new FileWriter(summaryFile, false);
            
            double annualTotal = 0.0;
            for (Map.Entry<String, Double> entry : monthlyData.entrySet()) {
                writer.write(entry.getKey() + separator + entry.getValue() + "\n");
                annualTotal += entry.getValue();
            }
            writer.write("ANNUAL_TOTAL" + separator + annualTotal + "\n");
            writer.close();
            System.out.println("? Saved all monthly consumption data");
        } catch (IOException e) {
            System.out.println("Error saving all monthly consumptions: " + e.getMessage());
        }
    }

    public Map<String, Double> loadAllMonthlyConsumptions() {
        Map<String, Double> data = new HashMap<>();
        String summaryFile = "annual_consumption_summary.txt";
        File f = new File(summaryFile);

        if (!f.exists()) {
            return data;
        }

        try {
            Scanner sc = new Scanner(f);
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (validateLine(line)) {
                    String[] parts = line.split(separator);
                    if (parts.length == 2) {
                        String month = parts[0].trim();
                        double consumption = Double.parseDouble(parts[1].trim());
                        data.put(month, consumption);
                    }
                }
            }
            sc.close();
        } catch (Exception e) {
            System.out.println("Error loading monthly consumptions: " + e.getMessage());
        }
        return data;
    }

    public double getAnnualConsumptionFromSummary() {
        Map<String, Double> data = loadAllMonthlyConsumptions();
        if (data.containsKey("ANNUAL_TOTAL")) {
            return data.get("ANNUAL_TOTAL");
        }
        return 0.0;
    }

    public List<Appliance> load() {
        List<Appliance> list = new ArrayList<>();

        if (!fileExists())
            return list;

        try {
            Scanner sc = new Scanner(new File(fileName));
            while (sc.hasNextLine()) {
                String line = sc.nextLine();

                if (validateLine(line)) {
                    Appliance a = parseAppliance(line);
                    if (a != null)
                        list.add(a);
                }
            }
            sc.close();
        } catch (Exception e) {
            System.out.println("Error loading the file");
        }
        return list;
    }

    public boolean validateLine(String line) {
        return line.contains(separator);
    }

    public Appliance parseAppliance(String line) {
        try {
            String[] parts = line.split(separator);
            String name = parts[0];
            double power = Double.parseDouble(parts[1]);
            // create an anonymous Appliance implementation
            Appliance a = new Appliance(name, power, 1.0, "Unknown", true) {
                @Override
                public double calculateDailyEnergy() {
                    return (getPowerUsage() / 1000.0) * getDailyUsageHours();
                }
                @Override
                public double calculateMonthlyEnergy() {
                    return calculateDailyEnergy() * 30;
                }
            };
            return a;
        } catch (Exception e) {
            return null;
        }
    }

    public void clearFile() {
        try {
            FileWriter writer = new FileWriter(fileName);
            writer.write("");
            writer.close();
        } catch (Exception e) {
            System.out.println("Error clearing the file");
        }
    }

    public boolean fileExists() {
        File f = new File(fileName);
        return f.exists();
    }
}
