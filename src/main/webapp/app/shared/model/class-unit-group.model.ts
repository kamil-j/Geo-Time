import { BaseEntity } from '..';

export class ClassUnitGroup implements BaseEntity {
    constructor(
        public id?: number,
        public name?: string,
        public description?: string,
    ) {
    }
}
