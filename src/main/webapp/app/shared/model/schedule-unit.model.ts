import { BaseEntity } from '..';

export class ScheduleUnit implements BaseEntity {
    constructor(
        public id?: number,
        public startDate?: any,
        public endDate?: any,
        public classUnitId?: number,
        public classUnitTitle?: string,
        public roomId?: number,
        public roomName?: string,
    ) {
    }
}
