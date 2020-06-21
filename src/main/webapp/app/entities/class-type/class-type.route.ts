import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiPaginationUtil } from 'ng-jhipster';

import { UserRouteAccessService } from '../../shared';
import { ClassTypeComponent } from './class-type.component';
import { ClassTypeDetailComponent } from './class-type-detail.component';
import { ClassTypePopupComponent } from './class-type-dialog.component';
import { ClassTypeDeletePopupComponent } from './class-type-delete-dialog.component';

@Injectable()
export class ClassTypeResolvePagingParams implements Resolve<any> {

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

export const classTypeRoute: Routes = [
    {
        path: 'class-type',
        component: ClassTypeComponent,
        resolve: {
            'pagingParams': ClassTypeResolvePagingParams
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'geoTimeApp.classType.home.title'
        },
        canActivate: [UserRouteAccessService]
    }, {
        path: 'class-type/:id',
        component: ClassTypeDetailComponent,
        data: {
            authorities: ['ROLE_ADMIN', 'ROLE_MANAGER'],
            pageTitle: 'geoTimeApp.classType.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const classTypePopupRoute: Routes = [
    {
        path: 'class-type-new',
        component: ClassTypePopupComponent,
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'geoTimeApp.classType.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'class-type/:id/edit',
        component: ClassTypePopupComponent,
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'geoTimeApp.classType.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'class-type/:id/delete',
        component: ClassTypeDeletePopupComponent,
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'geoTimeApp.classType.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
