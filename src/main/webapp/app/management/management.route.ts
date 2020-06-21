import { Routes } from '@angular/router';

import {
    userMgmtRoute,
    userDialogRoute,
    departmentPlanningMetricsRoute,
    subdepartmentPlanningMetricsRoutes
} from './';

import { UserRouteAccessService } from '../shared';

const MANAGEMENT_ROUTES = [
    departmentPlanningMetricsRoute,
    ...userMgmtRoute,
];

export const managementState: Routes = [{
    path: '',
    data: {
        authorities: ['ROLE_MANAGER', 'ROLE_PLANNER']
    },
    canActivate: [UserRouteAccessService],
    children: MANAGEMENT_ROUTES
},
    ...subdepartmentPlanningMetricsRoutes,
    ...userDialogRoute
];
