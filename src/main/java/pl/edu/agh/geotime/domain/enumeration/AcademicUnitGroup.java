package pl.edu.agh.geotime.domain.enumeration;

public enum AcademicUnitGroup {
    GROUP1, GROUP2, GROUP3, GROUP4, GROUP5, GROUP6, GROUP7, GROUP8;

    public static final String ALL_GROUPS = "ALL";

    public static AcademicUnitGroup fromGroupNumber(int groupNumber) {
        switch (groupNumber) {
            case 1: return GROUP1;
            case 2: return GROUP2;
            case 3: return GROUP3;
            case 4: return GROUP4;
            case 5: return GROUP5;
            case 6: return GROUP6;
            case 7: return GROUP7;
            case 8: return GROUP8;
            default: throw new IllegalStateException();
        }
    }
}
