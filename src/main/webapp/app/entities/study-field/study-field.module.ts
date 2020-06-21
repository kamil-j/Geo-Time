import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { GeoTimeSharedModule } from '../../shared';
import {
    StudyFieldService,
    StudyFieldPopupService,
    StudyFieldComponent,
    StudyFieldDetailComponent,
    StudyFieldDialogComponent,
    StudyFieldPopupComponent,
    StudyFieldDeletePopupComponent,
    StudyFieldDeleteDialogComponent,
    studyFieldRoute,
    studyFieldPopupRoute,
    StudyFieldResolvePagingParams,
} from './';

const ENTITY_STATES = [
    ...studyFieldRoute,
    ...studyFieldPopupRoute,
];

@NgModule({
    imports: [
        GeoTimeSharedModule,
        RouterModule.forChild(ENTITY_STATES)
    ],
    declarations: [
        StudyFieldComponent,
        StudyFieldDetailComponent,
        StudyFieldDialogComponent,
        StudyFieldDeleteDialogComponent,
        StudyFieldPopupComponent,
        StudyFieldDeletePopupComponent,
    ],
    entryComponents: [
        StudyFieldComponent,
        StudyFieldDialogComponent,
        StudyFieldPopupComponent,
        StudyFieldDeleteDialogComponent,
        StudyFieldDeletePopupComponent,
    ],
    providers: [
        StudyFieldService,
        StudyFieldPopupService,
        StudyFieldResolvePagingParams,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class GeoTimeStudyFieldModule {}
