import { BaseEntity } from '..';

export class UserGroup implements BaseEntity {
    constructor(
        public id?: number,
        public name?: string,
        public description?: string,
    ) {
    }
}
