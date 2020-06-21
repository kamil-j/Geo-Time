import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiPaginationUtil } from 'ng-jhipster';

import { UserRouteAccessService } from '../../shared';
import { RoomTypeComponent } from './room-type.component';
import { RoomTypeDetailComponent } from './room-type-detail.component';
import { RoomTypePopupComponent } from './room-type-dialog.component';
import { RoomTypeDeletePopupComponent } from './room-type-delete-dialog.component';

@Injectable()
export class RoomTypeResolvePagingParams implements Resolve<any> {

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

export const roomTypeRoute: Routes = [
    {
        path: 'room-type',
        component: RoomTypeComponent,
        resolve: {
            'pagingParams': RoomTypeResolvePagingParams
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'geoTimeApp.roomType.home.title'
        },
        canActivate: [UserRouteAccessService]
    }, {
        path: 'room-type/:id',
        component: RoomTypeDetailComponent,
        data: {
            authorities: ['ROLE_ADMIN', 'ROLE_MANAGER'],
            pageTitle: 'geoTimeApp.roomType.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const roomTypePopupRoute: Routes = [
    {
        path: 'room-type-new',
        component: RoomTypePopupComponent,
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'geoTimeApp.roomType.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'room-type/:id/edit',
        component: RoomTypePopupComponent,
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'geoTimeApp.roomType.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'room-type/:id/delete',
        component: RoomTypeDeletePopupComponent,
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'geoTimeApp.roomType.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
