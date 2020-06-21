import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiPaginationUtil } from 'ng-jhipster';

import { UserRouteAccessService } from '../../shared';
import { AcademicUnitComponent } from './academic-unit.component';
import { AcademicUnitDetailComponent } from './academic-unit-detail.component';
import { AcademicUnitPopupComponent } from './academic-unit-dialog.component';
import { AcademicUnitDeletePopupComponent } from './academic-unit-delete-dialog.component';

@Injectable()
export class AcademicUnitResolvePagingParams implements Resolve<any> {

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

export const academicUnitRoute: Routes = [
    {
        path: 'academic-unit',
        component: AcademicUnitComponent,
        resolve: {
            'pagingParams': AcademicUnitResolvePagingParams
        },
        data: {
            authorities: ['ROLE_MANAGER'],
            pageTitle: 'geoTimeApp.academicUnit.home.title'
        },
        canActivate: [UserRouteAccessService]
    }, {
        path: 'academic-unit/:id',
        component: AcademicUnitDetailComponent,
        data: {
            authorities: ['ROLE_MANAGER'],
            pageTitle: 'geoTimeApp.academicUnit.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const academicUnitPopupRoute: Routes = [
    {
        path: 'academic-unit-new',
        component: AcademicUnitPopupComponent,
        data: {
            authorities: ['ROLE_MANAGER'],
            pageTitle: 'geoTimeApp.academicUnit.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'academic-unit/:id/edit',
        component: AcademicUnitPopupComponent,
        data: {
            authorities: ['ROLE_MANAGER'],
            pageTitle: 'geoTimeApp.academicUnit.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'academic-unit/:id/delete',
        component: AcademicUnitDeletePopupComponent,
        data: {
            authorities: ['ROLE_MANAGER'],
            pageTitle: 'geoTimeApp.academicUnit.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
