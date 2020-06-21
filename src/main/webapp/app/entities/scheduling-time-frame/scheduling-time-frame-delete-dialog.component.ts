import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';
import { SchedulingTimeFrame } from '../../shared';
import { SchedulingTimeFramePopupService } from './scheduling-time-frame-popup.service';
import { SchedulingTimeFrameService } from './scheduling-time-frame.service';

@Component({
    selector: 'jhi-scheduling-time-frame-delete-dialog',
    templateUrl: './scheduling-time-frame-delete-dialog.component.html'
})
export class SchedulingTimeFrameDeleteDialogComponent {

    schedulingTimeFrame: SchedulingTimeFrame;

    constructor(
        private schedulingTimeFrameService: SchedulingTimeFrameService,
        public activeModal: NgbActiveModal,
        private eventManager: JhiEventManager
    ) {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.schedulingTimeFrameService.delete(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: 'schedulingTimeFrameListModification',
                content: 'Deleted an schedulingTimeFrame'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-scheduling-time-frame-delete-popup',
    template: ''
})
export class SchedulingTimeFrameDeletePopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private schedulingTimeFramePopupService: SchedulingTimeFramePopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.schedulingTimeFramePopupService
                .open(SchedulingTimeFrameDeleteDialogComponent as Component, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
