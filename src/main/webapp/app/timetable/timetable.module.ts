import {RouterModule} from '@angular/router';
import {TimetableComponent} from './timetable.component';
import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from '@angular/core';
import {TIMETABLE_ROUTE} from './timetable.route';
import {BrowserModule} from '@angular/platform-browser';
import {GeoTimeSharedModule} from '../shared';
import {TimetableService} from './timetable.service';
import {DraggableDirective} from './draggable/draggable.directive';
import {TimetableHelper} from './timetable.helper';

@NgModule({
    imports: [
        BrowserModule,
        GeoTimeSharedModule,
        RouterModule.forChild(TIMETABLE_ROUTE)
    ],
    declarations: [
        TimetableComponent,
        DraggableDirective
    ],
    entryComponents: [
    ],
    providers: [
        TimetableService,
        TimetableHelper
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class GeoTimeTimetableModule {}
