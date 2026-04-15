import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TipProvider {

    private List<Tip> tips;
    private Random random;

    public TipProvider() {
        tips = new ArrayList<>();
        random = new Random();
        initializeTips();
    }

    public void addTip(Tip tip) {
        tips.add(tip);
    }

    public List<Tip> getGeneralTips() {
        return new ArrayList<>(tips);
    }

    public Tip getTipForCategory(String category) {
        List<Tip> filtered = new ArrayList<>();
        for (Tip tip : tips) {
            if (tip.getCategory().equalsIgnoreCase(category)) {
                filtered.add(tip);
            }
        }
        if (filtered.isEmpty()) return null;

        int index = random.nextInt(filtered.size());
        return filtered.get(index);
    }
    private void initializeTips() {

        String[] offApplianceTips = {
                "Unplug when not in use to save power.",
                "Schedule usage and turn on only when needed.",
                "Use power strips to turn off multiple devices at once.",
                "Regularly clean and maintain appliances for efficiency.",
                "Avoid leaving appliances in standby mode."
        }
        ;
        String[] onApplianceTips = {
                "Use energy-efficient models to save energy.",
                "Regular cleaning and maintenance improves efficiency.",
                "Monitor energy usage to avoid waste.",
                "Optimize appliance settings (e.g., fridge temperature).",
                "Use timers or smart devices if safe to reduce work cycles."
        };
        for (String tipText : offApplianceTips) {
            addTip(new Tip(tipText, "CanBeTurnedOff", true));
        }
        for (String tipText : onApplianceTips) {
            addTip(new Tip(tipText, "ShouldStayOn", false));
        }
    }
}
