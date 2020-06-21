import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { GeoTimeSharedModule } from '../../shared';
import {
    ClassUnitGroupService,
    ClassUnitGroupPopupService,
    ClassUnitGroupComponent,
    ClassUnitGroupDetailComponent,
    ClassUnitGroupDialogComponent,
    ClassUnitGroupPopupComponent,
    ClassUnitGroupDeletePopupComponent,
    ClassUnitGroupDeleteDialogComponent,
    classUnitGroupRoute,
    classUnitGroupPopupRoute,
    ClassUnitGroupResolvePagingParams,
} from './';

const ENTITY_STATES = [
    ...classUnitGroupRoute,
    ...classUnitGroupPopupRoute,
];

@NgModule({
    imports: [
        GeoTimeSharedModule,
        RouterModule.forChild(ENTITY_STATES)
    ],
    declarations: [
        ClassUnitGroupComponent,
        ClassUnitGroupDetailComponent,
        ClassUnitGroupDialogComponent,
        ClassUnitGroupDeleteDialogComponent,
        ClassUnitGroupPopupComponent,
        ClassUnitGroupDeletePopupComponent,
    ],
    entryComponents: [
        ClassUnitGroupComponent,
        ClassUnitGroupDialogComponent,
        ClassUnitGroupPopupComponent,
        ClassUnitGroupDeleteDialogComponent,
        ClassUnitGroupDeletePopupComponent,
    ],
    providers: [
        ClassUnitGroupService,
        ClassUnitGroupPopupService,
        ClassUnitGroupResolvePagingParams,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class GeoTimeClassUnitGroupModule {}
