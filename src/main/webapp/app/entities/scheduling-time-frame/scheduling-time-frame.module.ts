import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { GeoTimeSharedModule } from '../../shared';
import {
    SchedulingTimeFrameService,
    SchedulingTimeFramePopupService,
    SchedulingTimeFrameComponent,
    SchedulingTimeFrameDetailComponent,
    SchedulingTimeFrameDialogComponent,
    SchedulingTimeFramePopupComponent,
    SchedulingTimeFrameDeletePopupComponent,
    SchedulingTimeFrameDeleteDialogComponent,
    schedulingTimeFrameRoute,
    schedulingTimeFramePopupRoute,
    SchedulingTimeFrameResolvePagingParams,
} from './';

const ENTITY_STATES = [
    ...schedulingTimeFrameRoute,
    ...schedulingTimeFramePopupRoute,
];

@NgModule({
    imports: [
        GeoTimeSharedModule,
        RouterModule.forChild(ENTITY_STATES)
    ],
    declarations: [
        SchedulingTimeFrameComponent,
        SchedulingTimeFrameDetailComponent,
        SchedulingTimeFrameDialogComponent,
        SchedulingTimeFrameDeleteDialogComponent,
        SchedulingTimeFramePopupComponent,
        SchedulingTimeFrameDeletePopupComponent,
    ],
    entryComponents: [
        SchedulingTimeFrameComponent,
        SchedulingTimeFrameDialogComponent,
        SchedulingTimeFramePopupComponent,
        SchedulingTimeFrameDeleteDialogComponent,
        SchedulingTimeFrameDeletePopupComponent,
    ],
    providers: [
        SchedulingTimeFrameService,
        SchedulingTimeFramePopupService,
        SchedulingTimeFrameResolvePagingParams,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class GeoTimeSchedulingTimeFrameModule {}
