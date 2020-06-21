import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { GeoTimeSharedModule } from '../../shared';
import {
    UserGroupService,
    UserGroupPopupService,
    UserGroupComponent,
    UserGroupDetailComponent,
    UserGroupDialogComponent,
    UserGroupPopupComponent,
    UserGroupDeletePopupComponent,
    UserGroupDeleteDialogComponent,
    userGroupRoute,
    userGroupPopupRoute,
    UserGroupResolvePagingParams,
} from './';

const ENTITY_STATES = [
    ...userGroupRoute,
    ...userGroupPopupRoute,
];

@NgModule({
    imports: [
        GeoTimeSharedModule,
        RouterModule.forChild(ENTITY_STATES)
    ],
    declarations: [
        UserGroupComponent,
        UserGroupDetailComponent,
        UserGroupDialogComponent,
        UserGroupDeleteDialogComponent,
        UserGroupPopupComponent,
        UserGroupDeletePopupComponent,
    ],
    entryComponents: [
        UserGroupComponent,
        UserGroupDialogComponent,
        UserGroupPopupComponent,
        UserGroupDeleteDialogComponent,
        UserGroupDeletePopupComponent,
    ],
    providers: [
        UserGroupService,
        UserGroupPopupService,
        UserGroupResolvePagingParams,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class GeoTimeUserGroupModule {}
