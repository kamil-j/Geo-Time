import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiPaginationUtil } from 'ng-jhipster';

import { UserRouteAccessService } from '../../shared';
import { StudyFieldComponent } from './study-field.component';
import { StudyFieldDetailComponent } from './study-field-detail.component';
import { StudyFieldPopupComponent } from './study-field-dialog.component';
import { StudyFieldDeletePopupComponent } from './study-field-delete-dialog.component';

@Injectable()
export class StudyFieldResolvePagingParams implements Resolve<any> {

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

export const studyFieldRoute: Routes = [
    {
        path: 'study-field',
        component: StudyFieldComponent,
        resolve: {
            'pagingParams': StudyFieldResolvePagingParams
        },
        data: {
            authorities: ['ROLE_MANAGER'],
            pageTitle: 'geoTimeApp.studyField.home.title'
        },
        canActivate: [UserRouteAccessService]
    }, {
        path: 'study-field/:id',
        component: StudyFieldDetailComponent,
        data: {
            authorities: ['ROLE_MANAGER'],
            pageTitle: 'geoTimeApp.studyField.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const studyFieldPopupRoute: Routes = [
    {
        path: 'study-field-new',
        component: StudyFieldPopupComponent,
        data: {
            authorities: ['ROLE_MANAGER'],
            pageTitle: 'geoTimeApp.studyField.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'study-field/:id/edit',
        component: StudyFieldPopupComponent,
        data: {
            authorities: ['ROLE_MANAGER'],
            pageTitle: 'geoTimeApp.studyField.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'study-field/:id/delete',
        component: StudyFieldDeletePopupComponent,
        data: {
            authorities: ['ROLE_MANAGER'],
            pageTitle: 'geoTimeApp.studyField.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
