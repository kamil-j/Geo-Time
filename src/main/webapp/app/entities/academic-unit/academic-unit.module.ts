import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { GeoTimeSharedModule } from '../../shared';
import {
    AcademicUnitService,
    AcademicUnitPopupService,
    AcademicUnitComponent,
    AcademicUnitDetailComponent,
    AcademicUnitDialogComponent,
    AcademicUnitPopupComponent,
    AcademicUnitDeletePopupComponent,
    AcademicUnitDeleteDialogComponent,
    academicUnitRoute,
    academicUnitPopupRoute,
    AcademicUnitResolvePagingParams,
} from './';

const ENTITY_STATES = [
    ...academicUnitRoute,
    ...academicUnitPopupRoute,
];

@NgModule({
    imports: [
        GeoTimeSharedModule,
        RouterModule.forChild(ENTITY_STATES)
    ],
    declarations: [
        AcademicUnitComponent,
        AcademicUnitDetailComponent,
        AcademicUnitDialogComponent,
        AcademicUnitDeleteDialogComponent,
        AcademicUnitPopupComponent,
        AcademicUnitDeletePopupComponent,
    ],
    entryComponents: [
        AcademicUnitComponent,
        AcademicUnitDialogComponent,
        AcademicUnitPopupComponent,
        AcademicUnitDeleteDialogComponent,
        AcademicUnitDeletePopupComponent,
    ],
    providers: [
        AcademicUnitService,
        AcademicUnitPopupService,
        AcademicUnitResolvePagingParams,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class GeoTimeAcademicUnitModule {}
