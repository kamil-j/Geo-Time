import { BaseEntity } from '..';

export class Room implements BaseEntity {
    constructor(
        public id?: number,
        public name?: string,
        public capacity?: number,
        public roomTypeId?: number,
        public roomTypeName?: string,
        public locationId?: number,
        public locationName?: string,
    ) {
    }
}
