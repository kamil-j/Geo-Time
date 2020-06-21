import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';
import { Room } from '../../shared';
import { RoomPopupService } from './room-popup.service';
import { RoomService } from './room.service';

@Component({
    selector: 'jhi-room-delete-dialog',
    templateUrl: './room-delete-dialog.component.html'
})
export class RoomDeleteDialogComponent {

    room: Room;

    constructor(
        private roomService: RoomService,
        public activeModal: NgbActiveModal,
        private eventManager: JhiEventManager
    ) {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.roomService.delete(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: 'roomListModification',
                content: 'Deleted an room'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-room-delete-popup',
    template: ''
})
export class RoomDeletePopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private roomPopupService: RoomPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.roomPopupService
                .open(RoomDeleteDialogComponent as Component, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
