import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { GeoTimeSharedModule } from '../../shared';
import {
    ScheduleUnitService,
    ScheduleUnitPopupService,
    ScheduleUnitComponent,
    ScheduleUnitDetailComponent,
    ScheduleUnitDialogComponent,
    ScheduleUnitPopupComponent,
    ScheduleUnitDeletePopupComponent,
    ScheduleUnitDeleteDialogComponent,
    scheduleUnitRoute,
    scheduleUnitPopupRoute,
    ScheduleUnitResolvePagingParams
} from './';

const ENTITY_STATES = [
    ...scheduleUnitRoute,
    ...scheduleUnitPopupRoute,
];

@NgModule({
    imports: [
        GeoTimeSharedModule,
        RouterModule.forChild(ENTITY_STATES)
    ],
    declarations: [
        ScheduleUnitComponent,
        ScheduleUnitDetailComponent,
        ScheduleUnitDialogComponent,
        ScheduleUnitDeleteDialogComponent,
        ScheduleUnitPopupComponent,
        ScheduleUnitDeletePopupComponent,
    ],
    entryComponents: [
        ScheduleUnitComponent,
        ScheduleUnitDialogComponent,
        ScheduleUnitPopupComponent,
        ScheduleUnitDeleteDialogComponent,
        ScheduleUnitDeletePopupComponent,
    ],
    providers: [
        ScheduleUnitService,
        ScheduleUnitPopupService,
        ScheduleUnitResolvePagingParams,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class GeoTimeScheduleUnitModule {}
