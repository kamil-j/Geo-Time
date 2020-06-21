import { BaseEntity } from '..';

export class Department implements BaseEntity {
    constructor(
        public id?: number,
        public name?: string,
        public shortName?: string,
        public functional?: boolean
    ) {
        this.functional = false;
    }
}
