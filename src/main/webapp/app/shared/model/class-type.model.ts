import { BaseEntity } from '..';

export class ClassType implements BaseEntity {
    constructor(
        public id?: number,
        public name?: string,
        public shortName?: string,
        public description?: string,
        public color?: string
    ) {
    }
}
