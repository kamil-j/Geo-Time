import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { GeoTimeSharedModule } from '../../shared';
import {
    RoomService,
    RoomPopupService,
    RoomComponent,
    RoomDetailComponent,
    RoomDialogComponent,
    RoomPopupComponent,
    RoomDeletePopupComponent,
    RoomDeleteDialogComponent,
    roomRoute,
    roomPopupRoute,
    RoomResolvePagingParams,
} from './';

const ENTITY_STATES = [
    ...roomRoute,
    ...roomPopupRoute,
];

@NgModule({
    imports: [
        GeoTimeSharedModule,
        RouterModule.forChild(ENTITY_STATES)
    ],
    declarations: [
        RoomComponent,
        RoomDetailComponent,
        RoomDialogComponent,
        RoomDeleteDialogComponent,
        RoomPopupComponent,
        RoomDeletePopupComponent,
    ],
    entryComponents: [
        RoomComponent,
        RoomDialogComponent,
        RoomPopupComponent,
        RoomDeleteDialogComponent,
        RoomDeletePopupComponent,
    ],
    providers: [
        RoomService,
        RoomPopupService,
        RoomResolvePagingParams,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class GeoTimeRoomModule {}
