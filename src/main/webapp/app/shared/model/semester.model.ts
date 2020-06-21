import { BaseEntity } from '..';

export class Semester implements BaseEntity {
    constructor(
        public id?: number,
        public name?: string,
        public startDate?: any,
        public endDate?: any,
        public active?: boolean,
    ) {
        this.active = false;
    }
}
