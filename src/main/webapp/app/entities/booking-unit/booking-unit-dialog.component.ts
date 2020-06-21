import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';
import { BookingUnit, ClassUnit, Room } from '../../shared';
import { BookingUnitPopupService } from './booking-unit-popup.service';
import { BookingUnitService } from './booking-unit.service';
import { ClassUnitService } from '../class-unit';
import { RoomService } from '../room';

@Component({
    selector: 'jhi-booking-unit-dialog',
    templateUrl: './booking-unit-dialog.component.html'
})
export class BookingUnitDialogComponent implements OnInit {

    bookingUnit: BookingUnit;
    isSaving: boolean;

    classunits: ClassUnit[];

    rooms: Room[];

    constructor(
        public activeModal: NgbActiveModal,
        private jhiAlertService: JhiAlertService,
        private bookingUnitService: BookingUnitService,
        private classUnitService: ClassUnitService,
        private roomService: RoomService,
        private eventManager: JhiEventManager
    ) {
    }

    ngOnInit() {
        this.isSaving = false;
        this.classUnitService.query({size: 1000})
            .subscribe((res: HttpResponse<ClassUnit[]>) => { this.classunits = res.body; }, (res: HttpErrorResponse) => this.onError(res.message));
        this.roomService.query({size: 1000})
            .subscribe((res: HttpResponse<Room[]>) => { this.rooms = res.body; }, (res: HttpErrorResponse) => this.onError(res.message));
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        if (this.bookingUnit.id !== undefined) {
            this.subscribeToSaveResponse(
                this.bookingUnitService.update(this.bookingUnit));
        } else {
            this.subscribeToSaveResponse(
                this.bookingUnitService.create(this.bookingUnit));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<BookingUnit>>) {
        result.subscribe((res: HttpResponse<BookingUnit>) =>
            this.onSaveSuccess(res.body), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess(result: BookingUnit) {
        this.eventManager.broadcast({ name: 'bookingUnitListModification', content: 'OK'});
        this.isSaving = false;
        this.activeModal.dismiss(result);
    }

    private onSaveError() {
        this.isSaving = false;
    }

    private onError(error: any) {
        this.jhiAlertService.error(error.message, null, null);
    }

    trackClassUnitById(index: number, item: ClassUnit) {
        return item.id;
    }

    trackRoomById(index: number, item: Room) {
        return item.id;
    }
}

@Component({
    selector: 'jhi-booking-unit-popup',
    template: ''
})
export class BookingUnitPopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private bookingUnitPopupService: BookingUnitPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.bookingUnitPopupService
                    .open(BookingUnitDialogComponent as Component, params['id']);
            } else {
                this.bookingUnitPopupService
                    .open(BookingUnitDialogComponent as Component);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
