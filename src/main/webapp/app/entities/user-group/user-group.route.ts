import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiPaginationUtil } from 'ng-jhipster';

import { UserRouteAccessService } from '../../shared';
import { UserGroupComponent } from './user-group.component';
import { UserGroupDetailComponent } from './user-group-detail.component';
import { UserGroupPopupComponent } from './user-group-dialog.component';
import { UserGroupDeletePopupComponent } from './user-group-delete-dialog.component';

@Injectable()
export class UserGroupResolvePagingParams implements Resolve<any> {

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

export const userGroupRoute: Routes = [
    {
        path: 'user-group',
        component: UserGroupComponent,
        resolve: {
            'pagingParams': UserGroupResolvePagingParams
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'geoTimeApp.userGroup.home.title'
        },
        canActivate: [UserRouteAccessService]
    }, {
        path: 'user-group/:id',
        component: UserGroupDetailComponent,
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'geoTimeApp.userGroup.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const userGroupPopupRoute: Routes = [
    {
        path: 'user-group-new',
        component: UserGroupPopupComponent,
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'geoTimeApp.userGroup.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'user-group/:id/edit',
        component: UserGroupPopupComponent,
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'geoTimeApp.userGroup.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'user-group/:id/delete',
        component: UserGroupDeletePopupComponent,
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'geoTimeApp.userGroup.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
