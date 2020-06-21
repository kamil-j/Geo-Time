import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';

import { JhiPaginationUtil } from 'ng-jhipster';

import { UserMgmtComponent } from './user-management.component';
import { UserMgmtDetailComponent } from './user-management-detail.component';
import { UserDialogComponent } from './user-management-dialog.component';
import { UserDeleteDialogComponent } from './user-management-delete-dialog.component';

import {UserRouteAccessService} from '../../shared';
import {UserUploadDialogComponent} from './user-management-upload-dialog.component';

@Injectable()
export class UserMgmtResolvePagingParams implements Resolve<any> {

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

export const userMgmtRoute: Routes = [
    {
        path: 'user-management',
        component: UserMgmtComponent,
        resolve: {
            'pagingParams': UserMgmtResolvePagingParams
        },
        data: {
            authorities: ['ROLE_MANAGER', 'ROLE_PLANNER'],
            pageTitle: 'userManagement.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'user-management/:login',
        component: UserMgmtDetailComponent,
        data: {
            authorities: ['ROLE_MANAGER', 'ROLE_PLANNER'],
            pageTitle: 'userManagement.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const userDialogRoute: Routes = [
    {
        path: 'user-management-new',
        component: UserDialogComponent,
        data: {
            authorities: ['ROLE_MANAGER']
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'user-management/:login/edit',
        component: UserDialogComponent,
        data: {
            authorities: ['ROLE_MANAGER']
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'user-management/:login/delete',
        component: UserDeleteDialogComponent,
        data: {
            authorities: ['ROLE_MANAGER']
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'user-management/upload',
        component: UserUploadDialogComponent,
        data: {
            authorities: ['ROLE_MANAGER']
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
];
