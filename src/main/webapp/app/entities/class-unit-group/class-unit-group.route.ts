import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiPaginationUtil } from 'ng-jhipster';

import { UserRouteAccessService } from '../../shared';
import { ClassUnitGroupComponent } from './class-unit-group.component';
import { ClassUnitGroupDetailComponent } from './class-unit-group-detail.component';
import { ClassUnitGroupPopupComponent } from './class-unit-group-dialog.component';
import { ClassUnitGroupDeletePopupComponent } from './class-unit-group-delete-dialog.component';

@Injectable()
export class ClassUnitGroupResolvePagingParams implements Resolve<any> {

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

export const classUnitGroupRoute: Routes = [
    {
        path: 'class-unit-group',
        component: ClassUnitGroupComponent,
        resolve: {
            'pagingParams': ClassUnitGroupResolvePagingParams
        },
        data: {
            authorities: ['ROLE_MANAGER'],
            pageTitle: 'geoTimeApp.classUnitGroup.home.title'
        },
        canActivate: [UserRouteAccessService]
    }, {
        path: 'class-unit-group/:id',
        component: ClassUnitGroupDetailComponent,
        data: {
            authorities: ['ROLE_MANAGER'],
            pageTitle: 'geoTimeApp.classUnitGroup.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const classUnitGroupPopupRoute: Routes = [
    {
        path: 'class-unit-group-new',
        component: ClassUnitGroupPopupComponent,
        data: {
            authorities: ['ROLE_MANAGER'],
            pageTitle: 'geoTimeApp.classUnitGroup.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'class-unit-group/:id/edit',
        component: ClassUnitGroupPopupComponent,
        data: {
            authorities: ['ROLE_MANAGER'],
            pageTitle: 'geoTimeApp.classUnitGroup.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'class-unit-group/:id/delete',
        component: ClassUnitGroupDeletePopupComponent,
        data: {
            authorities: ['ROLE_MANAGER'],
            pageTitle: 'geoTimeApp.classUnitGroup.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
