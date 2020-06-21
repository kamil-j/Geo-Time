import { BaseEntity } from '..';

export class SchedulingTimeFrame implements BaseEntity {
    constructor(
        public id?: number,
        public startTime?: any,
        public endTime?: any,
        public userGroupId?: number,
        public userGroupName?: string,
        public subdepartmentId?: number,
        public subdepartmentShortName?: string,
        public semesterId?: number,
        public semesterName?: string,
    ) {
    }
}
