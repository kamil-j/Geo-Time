import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';
import { ScheduleUnit, ClassUnit, Room } from '../../shared';
import { ScheduleUnitPopupService } from './schedule-unit-popup.service';
import { ScheduleUnitService } from './schedule-unit.service';
import { ClassUnitService } from '../class-unit';
import { RoomService } from '../room';

@Component({
    selector: 'jhi-schedule-unit-dialog',
    templateUrl: './schedule-unit-dialog.component.html'
})
export class ScheduleUnitDialogComponent implements OnInit {

    scheduleUnit: ScheduleUnit;
    isSaving: boolean;

    classunits: ClassUnit[];

    rooms: Room[];

    constructor(
        public activeModal: NgbActiveModal,
        private jhiAlertService: JhiAlertService,
        private scheduleUnitService: ScheduleUnitService,
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
        if (this.scheduleUnit.id !== undefined) {
            this.subscribeToSaveResponse(
                this.scheduleUnitService.update(this.scheduleUnit));
        } else {
            this.subscribeToSaveResponse(
                this.scheduleUnitService.create(this.scheduleUnit));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<ScheduleUnit>>) {
        result.subscribe((res: HttpResponse<ScheduleUnit>) =>
            this.onSaveSuccess(res.body), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess(result: ScheduleUnit) {
        this.eventManager.broadcast({ name: 'scheduleUnitListModification', content: 'OK'});
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
    selector: 'jhi-schedule-unit-popup',
    template: ''
})
export class ScheduleUnitPopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private scheduleUnitPopupService: ScheduleUnitPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.scheduleUnitPopupService
                    .open(ScheduleUnitDialogComponent as Component, params['id']);
            } else {
                this.scheduleUnitPopupService
                    .open(ScheduleUnitDialogComponent as Component);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
