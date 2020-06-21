import {BaseEntity} from '../shared';

export class ScheduleInfo implements BaseEntity {
    constructor(
        public id?: number,
        public title?: string,
        public start?: any,
        public end?: any,
        public locked?: boolean,
        public type?: string,
        public roomName?: string,
        public lecturerName?: string,
    ) {
        this.locked = false;
    }
}
