import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiPaginationUtil } from 'ng-jhipster';

import { UserRouteAccessService } from '../../shared';
import { BookingUnitComponent } from './booking-unit.component';
import { BookingUnitDetailComponent } from './booking-unit-detail.component';
import { BookingUnitPopupComponent } from './booking-unit-dialog.component';
import { BookingUnitDeletePopupComponent } from './booking-unit-delete-dialog.component';

@Injectable()
export class BookingUnitResolvePagingParams implements Resolve<any> {

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

export const bookingUnitRoute: Routes = [
    {
        path: 'booking-unit',
        component: BookingUnitComponent,
        resolve: {
            'pagingParams': BookingUnitResolvePagingParams
        },
        data: {
            authorities: ['ROLE_MANAGER', 'ROLE_PLANNER'],
            pageTitle: 'geoTimeApp.bookingUnit.home.title'
        },
        canActivate: [UserRouteAccessService]
    }, {
        path: 'booking-unit/:id',
        component: BookingUnitDetailComponent,
        data: {
            authorities: ['ROLE_MANAGER', 'ROLE_PLANNER'],
            pageTitle: 'geoTimeApp.bookingUnit.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const bookingUnitPopupRoute: Routes = [
    {
        path: 'booking-unit-new',
        component: BookingUnitPopupComponent,
        data: {
            authorities: ['ROLE_MANAGER', 'ROLE_PLANNER'],
            pageTitle: 'geoTimeApp.bookingUnit.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'booking-unit/:id/edit',
        component: BookingUnitPopupComponent,
        data: {
            authorities: ['ROLE_MANAGER', 'ROLE_PLANNER'],
            pageTitle: 'geoTimeApp.bookingUnit.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'booking-unit/:id/delete',
        component: BookingUnitDeletePopupComponent,
        data: {
            authorities: ['ROLE_MANAGER', 'ROLE_PLANNER'],
            pageTitle: 'geoTimeApp.bookingUnit.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
