class Tip {

    private String message;
    private String applianceCategory;
    private boolean requiresTurnOff;

    public Tip(String message, String applianceCategory, boolean requiresTurnOff) {
        this.message = message;
        this.applianceCategory = applianceCategory;
        this.requiresTurnOff = requiresTurnOff;
    }

    public String getMessage() {
        return message;
    }

    public String getCategory() {
        return applianceCategory;
    }

    public boolean requiresTurnOff() {
        return requiresTurnOff;
    }



}
