import { BaseEntity } from '../index';

export class AcademicUnitInfo implements BaseEntity {
    constructor(
        public id?: number,
        public name?: string,
        public year?: string,
        public degree?: string,
        public studyFieldId?: number,
        public studyFieldName?: string,
        public studyFieldShortName?: string,
        public departmentId?: number,
        public departmentName?: string,
        public departmentShortName?: string,
    ) {
    }
}
