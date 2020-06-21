import {SubdepartmentPlanningMetrics} from '../subdepartment-planning-metrics/subdepartment-planning-metrics.model';

export class DepartmentPlanningMetrics {
    constructor(
        public departmentId?: number,
        public departmentShortName?: string,
        public classQuantity?: number,
        public bookedClassQuantity?: number,
        public subdepartmentsPlanningMetrics?: SubdepartmentPlanningMetrics[]
    ) {
    }
}
