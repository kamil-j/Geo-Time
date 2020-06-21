import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';
import { RoomType } from '../../shared';
import { RoomTypePopupService } from './room-type-popup.service';
import { RoomTypeService } from './room-type.service';

@Component({
    selector: 'jhi-room-type-dialog',
    templateUrl: './room-type-dialog.component.html'
})
export class RoomTypeDialogComponent implements OnInit {

    roomType: RoomType;
    isSaving: boolean;

    constructor(
        public activeModal: NgbActiveModal,
        private roomTypeService: RoomTypeService,
        private eventManager: JhiEventManager
    ) {
    }

    ngOnInit() {
        this.isSaving = false;
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        if (this.roomType.id !== undefined) {
            this.subscribeToSaveResponse(
                this.roomTypeService.update(this.roomType));
        } else {
            this.subscribeToSaveResponse(
                this.roomTypeService.create(this.roomType));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<RoomType>>) {
        result.subscribe((res: HttpResponse<RoomType>) =>
            this.onSaveSuccess(res.body), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess(result: RoomType) {
        this.eventManager.broadcast({ name: 'roomTypeListModification', content: 'OK'});
        this.isSaving = false;
        this.activeModal.dismiss(result);
    }

    private onSaveError() {
        this.isSaving = false;
    }
}

@Component({
    selector: 'jhi-room-type-popup',
    template: ''
})
export class RoomTypePopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private roomTypePopupService: RoomTypePopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.roomTypePopupService
                    .open(RoomTypeDialogComponent as Component, params['id']);
            } else {
                this.roomTypePopupService
                    .open(RoomTypeDialogComponent as Component);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
