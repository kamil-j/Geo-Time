import {AcademicUnitGroup, ClassFrequency, ClassUnitGroup} from '..';

export class ClassUnitCreate {
    constructor(
        public title?: string,
        public classTypeId?: number,
        public duration?: number,
        public hoursQuantity?: number,
        public frequency?: ClassFrequency,
        public semesterId?: number,
        public description?: string,
        public academicUnitId?: number,
        public academicUnitGroup?: AcademicUnitGroup,
        public userId?: number,
        public roomIds?: number[],
        public subdepartmentId?: number,
        public classUnitGroup?: ClassUnitGroup,
    ) {
        this.classUnitGroup = new ClassUnitGroup();
    }
}
