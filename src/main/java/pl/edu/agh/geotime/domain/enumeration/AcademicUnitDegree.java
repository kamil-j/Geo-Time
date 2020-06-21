package pl.edu.agh.geotime.domain.enumeration;

public enum AcademicUnitDegree {
    DEGREE1("1D"), DEGREE2("2D");

    public static AcademicUnitDegree fromDegreeNumber(int degreeNumber) {
        switch (degreeNumber) {
            case 1: return DEGREE1;
            case 2: return DEGREE2;
            default: throw new IllegalStateException();
        }
    }

    private final String shortName;

    AcademicUnitDegree(String shortName) {
        this.shortName = shortName;
    }

    public String getShortName() {
        return shortName;
    }
}
