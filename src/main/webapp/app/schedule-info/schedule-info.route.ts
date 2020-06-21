import { Route } from '@angular/router';
import {ScheduleInfoComponent} from './schedule-info.component';

export const SCHEDULE_INFO_ROUTE: Route = {
    path: 'schedule-info/:id',
    component: ScheduleInfoComponent,
    data: {
        authorities: [],
        pageTitle: 'geoTimeApp.scheduleInfo.home.title'
    }
};
