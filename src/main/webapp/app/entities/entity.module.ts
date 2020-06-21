import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { GeoTimeLocationModule } from './location/location.module';
import { GeoTimeRoomTypeModule } from './room-type/room-type.module';
import { GeoTimeRoomModule } from './room/room.module';
import { GeoTimeDepartmentModule } from './department/department.module';
import { GeoTimeStudyFieldModule } from './study-field/study-field.module';
import { GeoTimeClassUnitModule } from './class-unit/class-unit.module';
import { GeoTimeClassTypeModule } from './class-type/class-type.module';
import { GeoTimeScheduleUnitModule } from './schedule-unit/schedule-unit.module';
import { GeoTimeSemesterModule } from './semester/semester.module';
import { GeoTimeAcademicUnitModule } from './academic-unit/academic-unit.module';
import { GeoTimeUserGroupModule } from './user-group/user-group.module';
import { GeoTimeSchedulingTimeFrameModule } from './scheduling-time-frame/scheduling-time-frame.module';
import { GeoTimeClassUnitGroupModule } from './class-unit-group/class-unit-group.module';
import { GeoTimeBookingUnitModule } from './booking-unit/booking-unit.module';
import { GeoTimeSubdepartmentModule } from './subdepartment/subdepartment.module';
/* jhipster-needle-add-entity-module-import - JHipster will add entity modules imports here */

@NgModule({
    imports: [
        GeoTimeLocationModule,
        GeoTimeRoomTypeModule,
        GeoTimeRoomModule,
        GeoTimeDepartmentModule,
        GeoTimeStudyFieldModule,
        GeoTimeClassUnitModule,
        GeoTimeClassTypeModule,
        GeoTimeScheduleUnitModule,
        GeoTimeSemesterModule,
        GeoTimeAcademicUnitModule,
        GeoTimeUserGroupModule,
        GeoTimeSchedulingTimeFrameModule,
        GeoTimeClassUnitGroupModule,
        GeoTimeBookingUnitModule,
        GeoTimeSubdepartmentModule,
        /* jhipster-needle-add-entity-module - JHipster will add entity modules here */
    ],
    declarations: [],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class GeoTimeEntityModule {}
