import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiPaginationUtil } from 'ng-jhipster';

import { UserRouteAccessService } from '../../shared';
import { ScheduleUnitComponent } from './schedule-unit.component';
import { ScheduleUnitDetailComponent } from './schedule-unit-detail.component';
import { ScheduleUnitPopupComponent } from './schedule-unit-dialog.component';
import { ScheduleUnitDeletePopupComponent } from './schedule-unit-delete-dialog.component';

@Injectable()
export class ScheduleUnitResolvePagingParams implements Resolve<any> {

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

export const scheduleUnitRoute: Routes = [
    {
        path: 'schedule-unit',
        component: ScheduleUnitComponent,
        resolve: {
            'pagingParams': ScheduleUnitResolvePagingParams
        },
        data: {
            authorities: ['ROLE_MANAGER'],
            pageTitle: 'geoTimeApp.scheduleUnit.home.title'
        },
        canActivate: [UserRouteAccessService]
    }, {
        path: 'schedule-unit/:id',
        component: ScheduleUnitDetailComponent,
        data: {
            authorities: ['ROLE_MANAGER'],
            pageTitle: 'geoTimeApp.scheduleUnit.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const scheduleUnitPopupRoute: Routes = [
    {
        path: 'schedule-unit-new',
        component: ScheduleUnitPopupComponent,
        data: {
            authorities: ['ROLE_MANAGER'],
            pageTitle: 'geoTimeApp.scheduleUnit.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'schedule-unit/:id/edit',
        component: ScheduleUnitPopupComponent,
        data: {
            authorities: ['ROLE_MANAGER'],
            pageTitle: 'geoTimeApp.scheduleUnit.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'schedule-unit/:id/delete',
        component: ScheduleUnitDeletePopupComponent,
        data: {
            authorities: ['ROLE_MANAGER'],
            pageTitle: 'geoTimeApp.scheduleUnit.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
