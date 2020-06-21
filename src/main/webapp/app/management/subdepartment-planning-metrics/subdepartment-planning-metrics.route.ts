import {Routes} from '@angular/router';

import { SubdepartmentPlanningMetricsComponent } from './subdepartment-planning-metrics.component';
import {UserRouteAccessService} from '../../shared';

export const subdepartmentPlanningMetricsRoutes: Routes = [{
    path: 'subdepartment-planning-metrics/:id',
    component: SubdepartmentPlanningMetricsComponent,
    data: {
        authorities: ['ROLE_MANAGER', 'ROLE_PLANNER'],
        pageTitle: 'geoTimeApp.subdepartmentPlanningMetrics.title'
    },
    canActivate: [UserRouteAccessService],
}, {
    path: 'subdepartment-planning-metrics',
    component: SubdepartmentPlanningMetricsComponent,
    data: {
        authorities: ['ROLE_MANAGER', 'ROLE_PLANNER'],
        pageTitle: 'geoTimeApp.subdepartmentPlanningMetrics.title'
    },
    canActivate: [UserRouteAccessService],
},
];
