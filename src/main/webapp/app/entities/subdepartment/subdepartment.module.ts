import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { GeoTimeSharedModule } from '../../shared';
import {
    SubdepartmentService,
    SubdepartmentPopupService,
    SubdepartmentComponent,
    SubdepartmentDetailComponent,
    SubdepartmentDialogComponent,
    SubdepartmentPopupComponent,
    SubdepartmentDeletePopupComponent,
    SubdepartmentDeleteDialogComponent,
    subdepartmentRoute,
    subdepartmentPopupRoute,
    SubdepartmentResolvePagingParams,
} from './';

const ENTITY_STATES = [
    ...subdepartmentRoute,
    ...subdepartmentPopupRoute,
];

@NgModule({
    imports: [
        GeoTimeSharedModule,
        RouterModule.forChild(ENTITY_STATES)
    ],
    declarations: [
        SubdepartmentComponent,
        SubdepartmentDetailComponent,
        SubdepartmentDialogComponent,
        SubdepartmentDeleteDialogComponent,
        SubdepartmentPopupComponent,
        SubdepartmentDeletePopupComponent,
    ],
    entryComponents: [
        SubdepartmentComponent,
        SubdepartmentDialogComponent,
        SubdepartmentPopupComponent,
        SubdepartmentDeleteDialogComponent,
        SubdepartmentDeletePopupComponent,
    ],
    providers: [
        SubdepartmentService,
        SubdepartmentPopupService,
        SubdepartmentResolvePagingParams,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class GeoTimeSubdepartmentModule {}
