import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { GeoTimeSharedModule } from '../../shared';
import {
    BookingUnitService,
    BookingUnitPopupService,
    BookingUnitComponent,
    BookingUnitDetailComponent,
    BookingUnitDialogComponent,
    BookingUnitPopupComponent,
    BookingUnitDeletePopupComponent,
    BookingUnitDeleteDialogComponent,
    bookingUnitRoute,
    bookingUnitPopupRoute,
    BookingUnitResolvePagingParams,
} from './';

const ENTITY_STATES = [
    ...bookingUnitRoute,
    ...bookingUnitPopupRoute,
];

@NgModule({
    imports: [
        GeoTimeSharedModule,
        RouterModule.forChild(ENTITY_STATES)
    ],
    declarations: [
        BookingUnitComponent,
        BookingUnitDetailComponent,
        BookingUnitDialogComponent,
        BookingUnitDeleteDialogComponent,
        BookingUnitPopupComponent,
        BookingUnitDeletePopupComponent,
    ],
    entryComponents: [
        BookingUnitComponent,
        BookingUnitDialogComponent,
        BookingUnitPopupComponent,
        BookingUnitDeleteDialogComponent,
        BookingUnitDeletePopupComponent,
    ],
    providers: [
        BookingUnitService,
        BookingUnitPopupService,
        BookingUnitResolvePagingParams,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class GeoTimeBookingUnitModule {}
