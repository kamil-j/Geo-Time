import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';
import { Room, Location, RoomType } from '../../shared';
import { RoomPopupService } from './room-popup.service';
import { RoomService } from './room.service';
import { RoomTypeService } from '../room-type';
import { LocationService } from '../location';

@Component({
    selector: 'jhi-room-dialog',
    templateUrl: './room-dialog.component.html'
})
export class RoomDialogComponent implements OnInit {

    room: Room;
    isSaving: boolean;

    roomtypes: RoomType[];

    locations: Location[];

    constructor(
        public activeModal: NgbActiveModal,
        private jhiAlertService: JhiAlertService,
        private roomService: RoomService,
        private roomTypeService: RoomTypeService,
        private locationService: LocationService,
        private eventManager: JhiEventManager
    ) {
    }

    ngOnInit() {
        this.isSaving = false;
        this.roomTypeService.query()
            .subscribe((res: HttpResponse<RoomType[]>) => { this.roomtypes = res.body; }, (res: HttpErrorResponse) => this.onError(res.message));
        this.locationService.query()
            .subscribe((res: HttpResponse<Location[]>) => { this.locations = res.body; }, (res: HttpErrorResponse) => this.onError(res.message));
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        if (this.room.id !== undefined) {
            this.subscribeToSaveResponse(
                this.roomService.update(this.room));
        } else {
            this.subscribeToSaveResponse(
                this.roomService.create(this.room));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<Room>>) {
        result.subscribe((res: HttpResponse<Room>) =>
            this.onSaveSuccess(res.body), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess(result: Room) {
        this.eventManager.broadcast({ name: 'roomListModification', content: 'OK'});
        this.isSaving = false;
        this.activeModal.dismiss(result);
    }

    private onSaveError() {
        this.isSaving = false;
    }

    private onError(error: any) {
        this.jhiAlertService.error(error.message, null, null);
    }

    trackRoomTypeById(index: number, item: RoomType) {
        return item.id;
    }

    trackLocationById(index: number, item: Location) {
        return item.id;
    }
}

@Component({
    selector: 'jhi-room-popup',
    template: ''
})
export class RoomPopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private roomPopupService: RoomPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.roomPopupService
                    .open(RoomDialogComponent as Component, params['id']);
            } else {
                this.roomPopupService
                    .open(RoomDialogComponent as Component);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
