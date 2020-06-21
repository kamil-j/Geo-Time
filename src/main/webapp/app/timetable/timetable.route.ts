import {Routes} from '@angular/router';
import {TimetableComponent} from './timetable.component';
import {UserRouteAccessService} from '../shared';

export const TIMETABLE_ROUTE: Routes = [{
    path: 'timetable',
    component: TimetableComponent,
    data: {
        authorities: ['ROLE_USER'],
        pageTitle: 'geoTimeApp.timetable.home.title'
    },
    canActivate: [UserRouteAccessService]
}, {
    path: 'timetable/:login',
    component: TimetableComponent,
    data: {
        authorities: ['ROLE_MANAGER', 'ROLE_PLANNER'],
        pageTitle: 'geoTimeApp.user.timetable.title'
    },
    canActivate: [UserRouteAccessService]
}];
