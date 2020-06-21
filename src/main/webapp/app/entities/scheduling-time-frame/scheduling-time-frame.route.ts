import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiPaginationUtil } from 'ng-jhipster';

import { UserRouteAccessService } from '../../shared';
import { SchedulingTimeFrameComponent } from './scheduling-time-frame.component';
import { SchedulingTimeFrameDetailComponent } from './scheduling-time-frame-detail.component';
import { SchedulingTimeFramePopupComponent } from './scheduling-time-frame-dialog.component';
import { SchedulingTimeFrameDeletePopupComponent } from './scheduling-time-frame-delete-dialog.component';

@Injectable()
export class SchedulingTimeFrameResolvePagingParams implements Resolve<any> {

    constructor(private paginationUtil: JhiPaginationUtil) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        const page = route.queryParams['page'] ? route.queryParams['page'] : '1';
        const sort = route.queryParams['sort'] ? route.queryParams['sort'] : 'id,asc';
        return {
            page: this.paginationUtil.parsePage(page),
            predicate: this.paginationUtil.parsePredicate(sort),
            ascending: this.paginationUtil.parseAscending(sort)
      };
    }
}

export const schedulingTimeFrameRoute: Routes = [
    {
        path: 'scheduling-time-frame',
        component: SchedulingTimeFrameComponent,
        resolve: {
            'pagingParams': SchedulingTimeFrameResolvePagingParams
        },
        data: {
            authorities: ['ROLE_MANAGER'],
            pageTitle: 'geoTimeApp.schedulingTimeFrame.home.title'
        },
        canActivate: [UserRouteAccessService]
    }, {
        path: 'scheduling-time-frame/:id',
        component: SchedulingTimeFrameDetailComponent,
        data: {
            authorities: ['ROLE_MANAGER'],
            pageTitle: 'geoTimeApp.schedulingTimeFrame.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const schedulingTimeFramePopupRoute: Routes = [
    {
        path: 'scheduling-time-frame-new',
        component: SchedulingTimeFramePopupComponent,
        data: {
            authorities: ['ROLE_MANAGER'],
            pageTitle: 'geoTimeApp.schedulingTimeFrame.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'scheduling-time-frame/:id/edit',
        component: SchedulingTimeFramePopupComponent,
        data: {
            authorities: ['ROLE_MANAGER'],
            pageTitle: 'geoTimeApp.schedulingTimeFrame.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'scheduling-time-frame/:id/delete',
        component: SchedulingTimeFrameDeletePopupComponent,
        data: {
            authorities: ['ROLE_MANAGER'],
            pageTitle: 'geoTimeApp.schedulingTimeFrame.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
