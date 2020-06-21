export class AcademicUnitGroupUtil {
    static convertToShortName(academicUnitGroupName: string) {
        switch (academicUnitGroupName) {
            case 'GROUP1': return 'G1';
            case 'GROUP2': return 'G2';
            case 'GROUP3': return 'G3';
            case 'GROUP4': return 'G4';
            case 'GROUP5': return 'G5';
            case 'GROUP6': return 'G6';
            case 'GROUP7': return 'G7';
            case 'GROUP8': return 'G8';
            case 'GROUP9': return 'G9';
            case 'GROUP10': return 'G10';
            default: return '';
        }
    }
}
