package pl.edu.agh.geotime.domain.enumeration;

public enum AcademicUnitYear {
    YEAR1("1Y"), YEAR2("2Y"), YEAR3("3Y"), YEAR4("4Y"), YEAR5("5Y");

    public static AcademicUnitYear fromYearNumber(int yearNumber) {
        switch (yearNumber) {
            case 1: return YEAR1;
            case 2: return YEAR2;
            case 3: return YEAR3;
            case 4: return YEAR4;
            case 5: return YEAR5;
            default: throw new IllegalStateException();
        }
    }

    private final String shortName;

    AcademicUnitYear(String shortName) {
        this.shortName = shortName;
    }

    public String getShortName() {
        return shortName;
    }
}
