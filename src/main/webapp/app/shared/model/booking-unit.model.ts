import { BaseEntity } from '..';

export const enum DayOfWeek {
    'MON',
    'TUE',
    'WED',
    'THU',
    'FRI'
}

export const enum WeekType {
    A = 'A',
    B = 'B'
}

export const enum SemesterHalf {
    HALF1 = 'HALF1',
    HALF2 = 'HALF2'
}

export class BookingUnit implements BaseEntity {
    constructor(
        public id?: number,
        public startTime?: any,
        public endTime?: any,
        public day?: DayOfWeek,
        public week?: WeekType,
        public semesterHalf?: SemesterHalf,
        public locked?: boolean,
        public classUnitId?: number,
        public classUnitTitle?: string,
        public roomId?: number,
        public roomName?: string,
    ) {
        this.locked = false;
    }
}
