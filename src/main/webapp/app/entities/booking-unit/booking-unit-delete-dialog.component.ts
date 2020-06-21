import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';
import { BookingUnit } from '../../shared';
import { BookingUnitPopupService } from './booking-unit-popup.service';
import { BookingUnitService } from './booking-unit.service';

@Component({
    selector: 'jhi-booking-unit-delete-dialog',
    templateUrl: './booking-unit-delete-dialog.component.html'
})
export class BookingUnitDeleteDialogComponent {

    bookingUnit: BookingUnit;

    constructor(
        private bookingUnitService: BookingUnitService,
        public activeModal: NgbActiveModal,
        private eventManager: JhiEventManager
    ) {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.bookingUnitService.delete(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: 'bookingUnitListModification',
                content: 'Deleted an bookingUnit'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-booking-unit-delete-popup',
    template: ''
})
export class BookingUnitDeletePopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private bookingUnitPopupService: BookingUnitPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.bookingUnitPopupService
                .open(BookingUnitDeleteDialogComponent as Component, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
