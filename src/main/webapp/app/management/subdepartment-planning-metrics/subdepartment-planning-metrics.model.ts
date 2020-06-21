import {UserPlanningMetrics} from './user-planning-metrics.model';

export class SubdepartmentPlanningMetrics {
    constructor(
        public subdepartmentId?: number,
        public subdepartmentShortName?: string,
        public classQuantity?: number,
        public bookedClassQuantity?: number,
        public usersPlanningMetrics?: UserPlanningMetrics[]
    ) {
    }
}
