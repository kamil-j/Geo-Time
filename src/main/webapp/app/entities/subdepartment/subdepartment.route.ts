import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiPaginationUtil } from 'ng-jhipster';

import { UserRouteAccessService } from '../../shared';
import { SubdepartmentComponent } from './subdepartment.component';
import { SubdepartmentDetailComponent } from './subdepartment-detail.component';
import { SubdepartmentPopupComponent } from './subdepartment-dialog.component';
import { SubdepartmentDeletePopupComponent } from './subdepartment-delete-dialog.component';

@Injectable()
export class SubdepartmentResolvePagingParams implements Resolve<any> {

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

export const subdepartmentRoute: Routes = [
    {
        path: 'subdepartment',
        component: SubdepartmentComponent,
        resolve: {
            'pagingParams': SubdepartmentResolvePagingParams
        },
        data: {
            authorities: ['ROLE_MANAGER'],
            pageTitle: 'geoTimeApp.subdepartment.home.title'
        },
        canActivate: [UserRouteAccessService]
    }, {
        path: 'subdepartment/:id',
        component: SubdepartmentDetailComponent,
        data: {
            authorities: ['ROLE_MANAGER'],
            pageTitle: 'geoTimeApp.subdepartment.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const subdepartmentPopupRoute: Routes = [
    {
        path: 'subdepartment-new',
        component: SubdepartmentPopupComponent,
        data: {
            authorities: ['ROLE_MANAGER'],
            pageTitle: 'geoTimeApp.subdepartment.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'subdepartment/:id/edit',
        component: SubdepartmentPopupComponent,
        data: {
            authorities: ['ROLE_MANAGER'],
            pageTitle: 'geoTimeApp.subdepartment.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'subdepartment/:id/delete',
        component: SubdepartmentDeletePopupComponent,
        data: {
            authorities: ['ROLE_MANAGER'],
            pageTitle: 'geoTimeApp.subdepartment.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
