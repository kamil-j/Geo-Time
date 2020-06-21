import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { GeoTimeSharedModule } from '../../shared';
import {
    ClassUnitService,
    ClassUnitPopupService,
    ClassUnitComponent,
    ClassUnitDetailComponent,
    ClassUnitDialogComponent,
    ClassUnitPopupComponent,
    ClassUnitDeletePopupComponent,
    ClassUnitDeleteDialogComponent,
    classUnitRoute,
    classUnitPopupRoute,
    ClassUnitResolvePagingParams,
    ClassUnitUploadDialogComponent,
    ClassUnitUploadPopupComponent,
    ClassUnitCreateComponent,
    ClassUnitCreateStep1Component,
    ClassUnitCreateStep2Component,
    ClassUnitCreateStep3Component,
    ClassUnitCreateStep4Component,
    ClassUnitAssignDialogComponent,
    ClassUnitAssignPopupComponent
} from './';
import {NgxSelectModule} from 'ngx-select-ex';

const ENTITY_STATES = [
    ...classUnitRoute,
    ...classUnitPopupRoute,
];

@NgModule({
    imports: [
        GeoTimeSharedModule,
        NgxSelectModule,
        RouterModule.forChild(ENTITY_STATES)
    ],
    declarations: [
        ClassUnitComponent,
        ClassUnitDetailComponent,
        ClassUnitDialogComponent,
        ClassUnitDeleteDialogComponent,
        ClassUnitUploadDialogComponent,
        ClassUnitAssignDialogComponent,
        ClassUnitCreateComponent,
        ClassUnitCreateStep1Component,
        ClassUnitCreateStep2Component,
        ClassUnitCreateStep3Component,
        ClassUnitCreateStep4Component,
        ClassUnitPopupComponent,
        ClassUnitDeletePopupComponent,
        ClassUnitUploadPopupComponent,
        ClassUnitAssignPopupComponent,
    ],
    entryComponents: [
        ClassUnitComponent,
        ClassUnitDialogComponent,
        ClassUnitPopupComponent,
        ClassUnitDeleteDialogComponent,
        ClassUnitUploadDialogComponent,
        ClassUnitAssignDialogComponent,
        ClassUnitDeletePopupComponent,
        ClassUnitUploadPopupComponent,
        ClassUnitAssignPopupComponent,
    ],
    providers: [
        ClassUnitService,
        ClassUnitPopupService,
        ClassUnitResolvePagingParams,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class GeoTimeClassUnitModule {}
