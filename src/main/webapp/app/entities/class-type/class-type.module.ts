import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ColorPickerModule } from 'ngx-color-picker';
import { GeoTimeSharedModule } from '../../shared';
import {
    ClassTypeService,
    ClassTypePopupService,
    ClassTypeComponent,
    ClassTypeDetailComponent,
    ClassTypeDialogComponent,
    ClassTypePopupComponent,
    ClassTypeDeletePopupComponent,
    ClassTypeDeleteDialogComponent,
    classTypeRoute,
    classTypePopupRoute,
    ClassTypeResolvePagingParams,
} from './';

const ENTITY_STATES = [
    ...classTypeRoute,
    ...classTypePopupRoute,
];

@NgModule({
    imports: [
        GeoTimeSharedModule,
        ColorPickerModule,
        RouterModule.forChild(ENTITY_STATES)
    ],
    declarations: [
        ClassTypeComponent,
        ClassTypeDetailComponent,
        ClassTypeDialogComponent,
        ClassTypeDeleteDialogComponent,
        ClassTypePopupComponent,
        ClassTypeDeletePopupComponent,
    ],
    entryComponents: [
        ClassTypeComponent,
        ClassTypeDialogComponent,
        ClassTypePopupComponent,
        ClassTypeDeleteDialogComponent,
        ClassTypeDeletePopupComponent,
    ],
    providers: [
        ClassTypeService,
        ClassTypePopupService,
        ClassTypeResolvePagingParams,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class GeoTimeClassTypeModule {}
