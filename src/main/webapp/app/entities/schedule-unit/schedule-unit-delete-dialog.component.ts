import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';
import { ScheduleUnit } from '../../shared';
import { ScheduleUnitPopupService } from './schedule-unit-popup.service';
import { ScheduleUnitService } from './schedule-unit.service';

@Component({
    selector: 'jhi-schedule-unit-delete-dialog',
    templateUrl: './schedule-unit-delete-dialog.component.html'
})
export class ScheduleUnitDeleteDialogComponent {

    scheduleUnit: ScheduleUnit;

    constructor(
        private scheduleUnitService: ScheduleUnitService,
        public activeModal: NgbActiveModal,
        private eventManager: JhiEventManager
    ) {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.scheduleUnitService.delete(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: 'scheduleUnitListModification',
                content: 'Deleted an scheduleUnit'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-schedule-unit-delete-popup',
    template: ''
})
export class ScheduleUnitDeletePopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private scheduleUnitPopupService: ScheduleUnitPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.scheduleUnitPopupService
                .open(ScheduleUnitDeleteDialogComponent as Component, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
