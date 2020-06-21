import { BaseEntity } from '..';

export const enum AcademicUnitYear {
    'YEAR1',
    'YEAR2',
    'YEAR3',
    'YEAR4',
    'YEAR5'
}

export const enum AcademicUnitDegree {
    'DEGREE1',
    'DEGREE2'
}

export class AcademicUnit implements BaseEntity {
    constructor(
        public id?: number,
        public name?: string,
        public year?: AcademicUnitYear,
        public degree?: AcademicUnitDegree,
        public description?: string,
        public studyFieldId?: number,
        public studyFieldName?: string,
    ) {
    }
}
