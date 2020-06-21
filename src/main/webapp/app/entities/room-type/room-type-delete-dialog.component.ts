import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';
import { RoomType } from '../../shared';
import { RoomTypePopupService } from './room-type-popup.service';
import { RoomTypeService } from './room-type.service';

@Component({
    selector: 'jhi-room-type-delete-dialog',
    templateUrl: './room-type-delete-dialog.component.html'
})
export class RoomTypeDeleteDialogComponent {

    roomType: RoomType;

    constructor(
        private roomTypeService: RoomTypeService,
        public activeModal: NgbActiveModal,
        private eventManager: JhiEventManager
    ) {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.roomTypeService.delete(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: 'roomTypeListModification',
                content: 'Deleted an roomType'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-room-type-delete-popup',
    template: ''
})
export class RoomTypeDeletePopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private roomTypePopupService: RoomTypePopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.roomTypePopupService
                .open(RoomTypeDeleteDialogComponent as Component, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
