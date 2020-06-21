import { BaseEntity } from '..';

export class StudyField implements BaseEntity {
    constructor(
        public id?: number,
        public name?: string,
        public shortName?: string,
    ) {
    }
}
