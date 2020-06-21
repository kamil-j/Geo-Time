import { Route } from '@angular/router';

import { DepartmentPlanningMetricsComponent } from './department-planning-metrics.component';
import {UserRouteAccessService} from '../../shared';

export const departmentPlanningMetricsRoute: Route = {
    path: 'department-planning-metrics',
    component: DepartmentPlanningMetricsComponent,
    data: {
        authorities: ['ROLE_MANAGER'],
        pageTitle: 'geoTimeApp.departmentPlanningMetrics.title'
    },
    canActivate: [UserRouteAccessService],
};
