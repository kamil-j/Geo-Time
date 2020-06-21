import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { GeoTimeSharedModule } from '../../shared';
import {
    RoomTypeService,
    RoomTypePopupService,
    RoomTypeComponent,
    RoomTypeDetailComponent,
    RoomTypeDialogComponent,
    RoomTypePopupComponent,
    RoomTypeDeletePopupComponent,
    RoomTypeDeleteDialogComponent,
    roomTypeRoute,
    roomTypePopupRoute,
    RoomTypeResolvePagingParams,
} from './';

const ENTITY_STATES = [
    ...roomTypeRoute,
    ...roomTypePopupRoute,
];

@NgModule({
    imports: [
        GeoTimeSharedModule,
        RouterModule.forChild(ENTITY_STATES)
    ],
    declarations: [
        RoomTypeComponent,
        RoomTypeDetailComponent,
        RoomTypeDialogComponent,
        RoomTypeDeleteDialogComponent,
        RoomTypePopupComponent,
        RoomTypeDeletePopupComponent,
    ],
    entryComponents: [
        RoomTypeComponent,
        RoomTypeDialogComponent,
        RoomTypePopupComponent,
        RoomTypeDeleteDialogComponent,
        RoomTypeDeletePopupComponent,
    ],
    providers: [
        RoomTypeService,
        RoomTypePopupService,
        RoomTypeResolvePagingParams,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class GeoTimeRoomTypeModule {}
