import { BaseEntity } from '..';

export enum ClassFrequency {
    SINGLE = 'SINGLE',
    EVERY_DAY = 'EVERY_DAY',
    EVERY_WEEK = 'EVERY_WEEK',
    EVERY_TWO_WEEKS = 'EVERY_TWO_WEEKS',
    EVERY_MONTH = 'EVERY_MONTH'
}

export const enum AcademicUnitGroup {
    'GROUP1',
    'GROUP2',
    'GROUP3',
    'GROUP4',
    'GROUP5',
    'GROUP6',
    'GROUP7',
    'GROUP8'
}

export class ClassUnit implements BaseEntity {
    constructor(
        public id?: number,
        public title?: string,
        public description?: string,
        public duration?: number,
        public hoursQuantity?: number,
        public frequency?: ClassFrequency,
        public academicUnitGroup?: AcademicUnitGroup,
        public onlySemesterHalf?: boolean,
        public classTypeId?: number,
        public classTypeName?: string,
        public userId?: number,
        public userLogin?: string,
        public rooms?: BaseEntity[],
        public academicUnitId?: number,
        public academicUnitName?: string,
        public semesterId?: number,
        public semesterName?: string,
        public classUnitGroupId?: number,
        public classUnitGroupName?: string,
        public subdepartmentId?: number,
        public subdepartmentShortName?: string,
        public relatedAcademicUnitGroups?: any[],
        public relatedUserIds?: number[],
        public selectedRoomId?: number,
    ) {
        this.onlySemesterHalf = false;
    }
}
