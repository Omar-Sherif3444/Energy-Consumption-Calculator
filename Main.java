import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.DecimalFormat;


public class Main {

    static abstract class Appliance {
        private String name;
        private double powerUsage; 
        private double dailyUsageHours;
        private String category;
        private boolean canBeTurnedOff;

        public Appliance(String name, double powerUsage, double dailyUsageHours, String category, boolean canBeTurnedOff) {
            this.name = name;
            this.powerUsage = powerUsage;
            this.dailyUsageHours = dailyUsageHours;
            this.category = category;
            this.canBeTurnedOff = canBeTurnedOff;
        }
        public String getName() { return name; }
        public double getPowerUsage() { return powerUsage; }
        public double getDailyUsageHours() { return dailyUsageHours; }
        public String getCategory() { return category; }
        public boolean isTurnOffPossible() { return canBeTurnedOff; }
        public abstract double calculateDailyEnergy();
        public abstract double calculateMonthlyEnergy();
    }

    static class SimpleAppliance extends Appliance {
        public SimpleAppliance(String name, double powerUsage, double hours, String category, boolean canBeTurnedOff) {
            super(name, powerUsage, hours, category, canBeTurnedOff);
        }
        @Override
        public double calculateDailyEnergy() {
            return (getPowerUsage() / 1000.0) * getDailyUsageHours();
        }
        @Override
        public double calculateMonthlyEnergy() {
            return calculateDailyEnergy() * 30;
        }
    }

    static class ApplianceManager {
        private final java.util.List<Appliance> appliances = new java.util.ArrayList<>();
        public void addAppliance(Appliance a) { appliances.add(a); }
        public boolean removeAppliance(String name) {
            java.util.Iterator<Appliance> it = appliances.iterator();
            while (it.hasNext()) {
                if (it.next().getName().equalsIgnoreCase(name)) {
                    it.remove();
                    return true;
                }
            }
            return false;
        }
        public java.util.List<Appliance> getAllAppliances() { return appliances; }
        public Appliance getMostConsuming() {
            if (appliances.isEmpty()) return null;
            Appliance max = appliances.get(0);
            for (Appliance a : appliances)
                if (a.calculateMonthlyEnergy() > max.calculateMonthlyEnergy())
                    max = a;
            return max;
        }
    }

    static class En {
        static double calculateMonthlyConsumption(Appliance a) {
            return (a.getPowerUsage() / 1000.0) * a.getDailyUsageHours() * 30;
        }
        static double calculateTotalConsumption(java.util.List<Appliance> apps) {
            double sum = 0;
            for (Appliance a : apps) sum += calculateMonthlyConsumption(a);
            return sum;
        }
        static double calculateTotalCost(double price, java.util.List<Appliance> apps) {
            return calculateTotalConsumption(apps) * price;
        }
    }

    interface CarbonCalculator { double calculateCO2(double kWh); }
    static class DefaultCarbonCalculator implements CarbonCalculator {
        private final double factor;
        public DefaultCarbonCalculator(double factor) { this.factor = factor; }
        public double calculateCO2(double kWh) { return kWh * factor; }
    }

    static class TipProvider {
        private final java.util.Map<String, java.util.List<String>> tips = new java.util.HashMap<>();
        public TipProvider() {
            tips.put("CanBeTurnedOff", java.util.Arrays.asList(
                    "Turn off when not in use to save energy.",
                    "Use a smart plug to schedule off times."
            ));
            tips.put("ShouldStayOn", java.util.Arrays.asList(
                    "Upgrade to a more efficient model if possible.",
                    "Keep regular maintenance to improve efficiency."
            ));
            tips.put("default", java.util.Arrays.asList(
                    "Use energy-efficient appliances.",
                    "Unplug chargers when not in use."
            ));
        }
        public String getTip(String category) {
            java.util.List<String> list = tips.getOrDefault(category, tips.get("default"));
            return list.get(new java.util.Random().nextInt(list.size()));
        }
    }

    static class FileManager {
        private final String summaryFile;
        public FileManager(String summaryFile) { this.summaryFile = summaryFile; }
        public boolean saveMonthly(String month, double kWh) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(summaryFile, true))) {
                pw.println(month + "," + kWh);
            } catch (IOException e) { return false; }
            return true;
        }
        public boolean saveAll(java.util.Map<String, Double> map, String outFile) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(outFile, false))) {
                double sum = 0;
                for (java.util.Map.Entry<String, Double> e : map.entrySet()) {
                    pw.println(e.getKey() + "," + e.getValue());
                    sum += e.getValue();
                }
                pw.println("TOTAL," + sum);
            } catch (IOException e) { return false; }
            return true;
        }
    }

    private final ApplianceManager manager = new ApplianceManager();
    private final TipProvider tipProvider = new TipProvider();
    private final FileManager fileManager = new FileManager("monthly_saved.csv");
    private final DecimalFormat df = new DecimalFormat("#0.00");

    private JFrame frame;
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextArea logArea;

    private JTextField nameField, powerField, hoursField, categoryField;
    private JCheckBox canTurnOffBox;
    private JTextField priceField, emissionField;
    private java.util.Map<String, Double> monthlySaved = new java.util.LinkedHashMap<>();

    public Main() {
        SwingUtilities.invokeLater(this::createAndShowGui);
    }

    private void createAndShowGui() {
        frame = new JFrame("Energy Consumption Calculator (GUI)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLayout(new BorderLayout(8,8));

        JPanel top = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4,4,4,4);
        c.anchor = GridBagConstraints.WEST;

        nameField = new JTextField(10);
        powerField = new JTextField(6);
        hoursField = new JTextField(4);
        categoryField = new JTextField(8);
        canTurnOffBox = new JCheckBox("Can be turned off");

        c.gridx = 0; c.gridy = 0; top.add(new JLabel("Name:"), c);
        c.gridx = 1; top.add(nameField, c);
        c.gridx = 2; top.add(new JLabel("Power (W):"), c);
        c.gridx = 3; top.add(powerField, c);
        c.gridx = 4; top.add(new JLabel("Daily hours:"), c);
        c.gridx = 5; top.add(hoursField, c);

        c.gridy = 1; c.gridx = 0; top.add(new JLabel("Category:"), c);
        c.gridx = 1; top.add(categoryField, c);
        c.gridx = 2; top.add(canTurnOffBox, c);

        JButton addBtn = new JButton("Add Appliance");
        addBtn.addActionListener(e -> onAddAppliance());
        c.gridx = 4; c.gridy = 1; top.add(addBtn, c);

        JButton removeBtn = new JButton("Remove Selected");
        removeBtn.addActionListener(e -> onRemoveSelected());
        c.gridx = 5; top.add(removeBtn, c);

        frame.add(top, BorderLayout.NORTH);

    
        String[] cols = {"Name","Category","Power(W)","Daily hrs","Daily kWh","Monthly kWh","Can turn off"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        frame.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setPreferredSize(new Dimension(320, 0));

        JPanel pricePanel = new JPanel(new GridLayout(2,2,4,4));
        pricePanel.setBorder(BorderFactory.createTitledBorder("Pricing & Emission"));
        priceField = new JTextField("0.20"); 
        emissionField = new JTextField("0.5"); 
        pricePanel.add(new JLabel("Price $/kWh:")); pricePanel.add(priceField);
        pricePanel.add(new JLabel("Emission kgCO2/kWh:")); pricePanel.add(emissionField);
        right.add(pricePanel);

        JButton analysisBtn = new JButton("Energy Analysis");
        analysisBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        analysisBtn.addActionListener(e -> onAnalysis());
        right.add(analysisBtn);

        JButton renewableBtn = new JButton("Renewable Tools");
        renewableBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        renewableBtn.addActionListener(e -> onRenewable());
        right.add(renewableBtn);

        JPanel savePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton saveMonthlyBtn = new JButton("Save Monthly Consumption");
        saveMonthlyBtn.addActionListener(e -> onSaveMonthly());
        JButton saveSummaryBtn = new JButton("Save Annual Summary");
        saveSummaryBtn.addActionListener(e -> onSaveSummary());
        savePanel.add(saveMonthlyBtn); savePanel.add(saveSummaryBtn);
        right.add(savePanel);

        right.add(Box.createRigidArea(new Dimension(0,8)));
        right.add(new JLabel("Tips & Log:"));
        logArea = new JTextArea(12, 25);
        logArea.setEditable(false);
        JScrollPane logScroll = new JScrollPane(logArea);
        right.add(logScroll);

        frame.add(right, BorderLayout.EAST);

        JLabel help = new JLabel("Select an appliance row, then click Remove Selected. All fields validated.");
        frame.add(help, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void onAddAppliance() {
        String name = nameField.getText().trim();
        String powerS = powerField.getText().trim();
        String hoursS = hoursField.getText().trim();
        String category = categoryField.getText().trim();
        boolean canOff = canTurnOffBox.isSelected();

        if (name.isEmpty() || powerS.isEmpty() || hoursS.isEmpty() || category.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please fill all fields.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        double power, hours;
        try {
            power = Double.parseDouble(powerS);
            hours = Double.parseDouble(hoursS);
            if (power < 0 || hours < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Power and hours must be non-negative numbers.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        SimpleAppliance app = new SimpleAppliance(name, power, hours, category, canOff);
        manager.addAppliance(app);
        addRowToTable(app);
        log("Added: " + name);
        clearInputFields();
    }

    private void addRowToTable(Appliance a) {
        tableModel.addRow(new Object[]{
                a.getName(),
                a.getCategory(),
                df.format(a.getPowerUsage()),
                df.format(a.getDailyUsageHours()),
                df.format(a.calculateDailyEnergy()),
                df.format(a.calculateMonthlyEnergy()),
                a.isTurnOffPossible() ? "Yes" : "No"
        });
    }

    private void clearInputFields() {
        nameField.setText("");
        powerField.setText("");
        hoursField.setText("");
        categoryField.setText("");
        canTurnOffBox.setSelected(false);
    }

    private void onRemoveSelected() {
        int sel = table.getSelectedRow();
        if (sel < 0) { JOptionPane.showMessageDialog(frame, "Select a row to remove."); return; }
        String name = tableModel.getValueAt(sel, 0).toString();
        boolean removed = manager.removeAppliance(name);
        if (removed) {
            tableModel.removeRow(sel);
            log("Removed: " + name);
        } else {
            JOptionPane.showMessageDialog(frame, "Could not remove appliance (not found).");
        }
    }

    private void onAnalysis() {
        java.util.List<Appliance> apps = manager.getAllAppliances();
        if (apps.isEmpty()) { JOptionPane.showMessageDialog(frame, "No appliances added."); return; }
        double total = En.calculateTotalConsumption(apps);
        double price;
        double emission;
        try {
            price = Double.parseDouble(priceField.getText().trim());
            emission = Double.parseDouble(emissionField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Price and emission must be numbers.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        double cost = En.calculateTotalCost(price, apps);
        DefaultCarbonCalculator calculator = new DefaultCarbonCalculator(emission);
        double carbon = calculator.calculateCO2(total);

        Appliance most = manager.getMostConsuming();
        StringBuilder sb = new StringBuilder();
        sb.append("Monthly energy: ").append(df.format(total)).append(" kWh\n");
        sb.append("Monthly cost: $").append(df.format(cost)).append("\n");
        sb.append("Carbon footprint: ").append(df.format(carbon)).append(" kg CO2\n");
        if (most != null) {
            sb.append("\nMost consuming: ").append(most.getName()).append(" — ")
              .append(df.format(most.calculateMonthlyEnergy())).append(" kWh/month\n");
            String tipCat = most.isTurnOffPossible() ? "CanBeTurnedOff" : "ShouldStayOn";
            sb.append("Tip: ").append(tipProvider.getTip(tipCat)).append("\n");
        }
        JOptionPane.showMessageDialog(frame, sb.toString(), "Energy Analysis", JOptionPane.INFORMATION_MESSAGE);
        log(sb.toString());
    }

    private void onRenewable() {
        String solarS = JOptionPane.showInputDialog(frame, "Enter solar kWh/month:", "0");
        if (solarS == null) return;
        String windS = JOptionPane.showInputDialog(frame, "Enter wind kWh/month:", "0");
        if (windS == null) return;
        double solar, wind, price;
        try {
            solar = Double.parseDouble(solarS.trim());
            wind = Double.parseDouble(windS.trim());
            price = Double.parseDouble(priceField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Numeric values required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        double totalRenew = solar + wind;
        double totalConsumption = En.calculateTotalConsumption(manager.getAllAppliances());
        double percent = totalConsumption == 0 ? 0.0 : (totalRenew / totalConsumption) * 100.0;
        double savings = totalRenew * price;

        StringBuilder sb = new StringBuilder();
        sb.append("Renewable generation: ").append(df.format(totalRenew)).append(" kWh/month\n");
        sb.append("Covers ").append(df.format(percent)).append("% of your monthly consumption\n");
        sb.append("Estimated monthly savings: $").append(df.format(savings)).append("\n\n");
        sb.append("Tips:\n• Install rooftop solar panels to reduce electricity costs.\n");
        sb.append("• Improve home insulation to lower heating/cooling energy.\n");
        sb.append("• Use high-efficiency LED lighting to cut consumption.\n");

        JOptionPane.showMessageDialog(frame, sb.toString(), "Renewable Advisor", JOptionPane.INFORMATION_MESSAGE);
        log(sb.toString());
    }

    private void onSaveMonthly() {
        java.util.List<Appliance> apps = manager.getAllAppliances();
        if (apps.isEmpty()) { JOptionPane.showMessageDialog(frame, "No appliances to calculate consumption."); return; }
        String month = JOptionPane.showInputDialog(frame, "Enter month name (e.g., January):");
        if (month == null || month.trim().isEmpty()) return;
        double total = En.calculateTotalConsumption(apps);
        if (fileManager.saveMonthly(month.trim(), total)) {
            monthlySaved.put(month.trim(), total);
            log("Saved " + df.format(total) + " kWh for " + month.trim());
            JOptionPane.showMessageDialog(frame, "Saved " + df.format(total) + " kWh for " + month.trim());
        } else {
            JOptionPane.showMessageDialog(frame, "Failed to save monthly consumption.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onSaveSummary() {
        if (monthlySaved.isEmpty()) { JOptionPane.showMessageDialog(frame, "No monthly data saved yet."); return; }
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("annual_summary.csv"));
        int res = chooser.showSaveDialog(frame);
        if (res != JFileChooser.APPROVE_OPTION) return;
        File out = chooser.getSelectedFile();
        if (fileManager.saveAll(monthlySaved, out.getAbsolutePath())) {
            double total = 0;
            for (Double d : monthlySaved.values()) total += d;
            log("Annual summary saved to " + out.getAbsolutePath() + " (total " + df.format(total) + " kWh)");
            JOptionPane.showMessageDialog(frame, "Saved summary. Total annual consumption: " + df.format(total) + " kWh");
        } else {
            JOptionPane.showMessageDialog(frame, "Failed to save summary.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void log(String s) {
        logArea.append(s + "\n\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    public static void main(String[] args) {
    
        new Main();
    }
}
