import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiPaginationUtil } from 'ng-jhipster';

import { UserRouteAccessService } from '../../shared';
import { RoomComponent } from './room.component';
import { RoomDetailComponent } from './room-detail.component';
import { RoomPopupComponent } from './room-dialog.component';
import { RoomDeletePopupComponent } from './room-delete-dialog.component';

@Injectable()
export class RoomResolvePagingParams implements Resolve<any> {

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

export const roomRoute: Routes = [
    {
        path: 'room',
        component: RoomComponent,
        resolve: {
            'pagingParams': RoomResolvePagingParams
        },
        data: {
            authorities: ['ROLE_MANAGER'],
            pageTitle: 'geoTimeApp.room.home.title'
        },
        canActivate: [UserRouteAccessService]
    }, {
        path: 'room/:id',
        component: RoomDetailComponent,
        data: {
            authorities: ['ROLE_MANAGER'],
            pageTitle: 'geoTimeApp.room.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const roomPopupRoute: Routes = [
    {
        path: 'room-new',
        component: RoomPopupComponent,
        data: {
            authorities: ['ROLE_MANAGER'],
            pageTitle: 'geoTimeApp.room.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'room/:id/edit',
        component: RoomPopupComponent,
        data: {
            authorities: ['ROLE_MANAGER'],
            pageTitle: 'geoTimeApp.room.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'room/:id/delete',
        component: RoomDeletePopupComponent,
        data: {
            authorities: ['ROLE_MANAGER'],
            pageTitle: 'geoTimeApp.room.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
