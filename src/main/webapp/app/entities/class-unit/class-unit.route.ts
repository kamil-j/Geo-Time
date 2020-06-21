import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiPaginationUtil } from 'ng-jhipster';

import { UserRouteAccessService } from '../../shared';
import { ClassUnitComponent } from './class-unit.component';
import { ClassUnitDetailComponent } from './class-unit-detail.component';
import { ClassUnitPopupComponent } from './class-unit-dialog.component';
import { ClassUnitDeletePopupComponent } from './class-unit-delete-dialog.component';
import {ClassUnitUploadPopupComponent} from './class-unit-upload-dialog.component';
import {ClassUnitCreateComponent} from './create/class-unit-create.component';
import {ClassUnitAssignPopupComponent} from './assign/class-unit-assign-dialog.component';

@Injectable()
export class ClassUnitResolvePagingParams implements Resolve<any> {

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

export const classUnitRoute: Routes = [
    {
        path: 'class-unit',
        component: ClassUnitComponent,
        resolve: {
            'pagingParams': ClassUnitResolvePagingParams
        },
        data: {
            authorities: ['ROLE_MANAGER', 'ROLE_PLANNER'],
            pageTitle: 'geoTimeApp.classUnit.home.title'
        },
        canActivate: [UserRouteAccessService]
    }, {
        path: 'class-unit/:id',
        component: ClassUnitDetailComponent,
        data: {
            authorities: ['ROLE_MANAGER', 'ROLE_PLANNER'],
            pageTitle: 'geoTimeApp.classUnit.home.title'
        },
        canActivate: [UserRouteAccessService]
    }, {
        path: 'class-unit-new',
        component: ClassUnitCreateComponent,
        data: {
            authorities: ['ROLE_MANAGER'],
            pageTitle: 'geoTimeApp.classUnit.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
];

export const classUnitPopupRoute: Routes = [
    {
        path: 'class-unit/:id/edit',
        component: ClassUnitPopupComponent,
        data: {
            authorities: ['ROLE_MANAGER'],
            pageTitle: 'geoTimeApp.classUnit.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'class-unit/:id/delete',
        component: ClassUnitDeletePopupComponent,
        data: {
            authorities: ['ROLE_MANAGER'],
            pageTitle: 'geoTimeApp.classUnit.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'class-unit/:id/assign',
        component: ClassUnitAssignPopupComponent,
        data: {
            authorities: ['ROLE_PLANNER'],
            pageTitle: 'geoTimeApp.classUnit.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'class-unit/upload',
        component: ClassUnitUploadPopupComponent,
        data: {
            authorities: ['ROLE_MANAGER'],
            pageTitle: 'geoTimeApp.classUnit.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
