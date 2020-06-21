import { BaseEntity } from '..';

export class DepartmentInfo implements BaseEntity {
    constructor(
        public id?: number,
        public name?: string,
        public shortName?: string,
        public studyFields?: BaseEntity[],
    ) {
    }
}
