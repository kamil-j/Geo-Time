import { BaseEntity } from '..';

export class RoomType implements BaseEntity {
    constructor(
        public id?: number,
        public name?: string,
        public description?: string
    ) {
    }
}
