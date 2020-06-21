import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import {SCHEDULE_INFO_ROUTE} from './schedule-info.route';
import {ScheduleInfoComponent} from './schedule-info.component';
import {BrowserModule} from '@angular/platform-browser';
import {GeoTimeSharedModule} from '../shared';
import {ScheduleInfoHelper} from './schedule-info.helper';

@NgModule({
    imports: [
        BrowserModule,
        GeoTimeSharedModule,
        RouterModule.forChild([ SCHEDULE_INFO_ROUTE ])
    ],
    declarations: [
        ScheduleInfoComponent,
    ],
    entryComponents: [
        ScheduleInfoComponent,
    ],
    providers: [
        ScheduleInfoHelper,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class GeoTimeScheduleInfoModule {}
