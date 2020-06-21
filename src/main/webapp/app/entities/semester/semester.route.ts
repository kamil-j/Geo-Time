import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiPaginationUtil } from 'ng-jhipster';

import { UserRouteAccessService } from '../../shared';
import { SemesterComponent } from './semester.component';
import { SemesterDetailComponent } from './semester-detail.component';
import { SemesterPopupComponent } from './semester-dialog.component';
import { SemesterDeletePopupComponent } from './semester-delete-dialog.component';

@Injectable()
export class SemesterResolvePagingParams implements Resolve<any> {

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

export const semesterRoute: Routes = [
    {
        path: 'semester',
        component: SemesterComponent,
        resolve: {
            'pagingParams': SemesterResolvePagingParams
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'geoTimeApp.semester.home.title'
        },
        canActivate: [UserRouteAccessService]
    }, {
        path: 'semester/:id',
        component: SemesterDetailComponent,
        data: {
            authorities: ['ROLE_ADMIN', 'ROLE_MANAGER'],
            pageTitle: 'geoTimeApp.semester.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const semesterPopupRoute: Routes = [
    {
        path: 'semester-new',
        component: SemesterPopupComponent,
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'geoTimeApp.semester.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'semester/:id/edit',
        component: SemesterPopupComponent,
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'geoTimeApp.semester.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'semester/:id/delete',
        component: SemesterDeletePopupComponent,
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'geoTimeApp.semester.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
