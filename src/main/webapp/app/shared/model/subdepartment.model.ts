import { BaseEntity } from '..';

export class Subdepartment implements BaseEntity {
    constructor(
        public id?: number,
        public name?: string,
        public shortName?: string,
    ) {
    }
}
