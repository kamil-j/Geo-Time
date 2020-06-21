import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';
import { AcademicUnit } from '../../shared';
import { AcademicUnitPopupService } from './academic-unit-popup.service';
import { AcademicUnitService } from './academic-unit.service';

@Component({
    selector: 'jhi-academic-unit-delete-dialog',
    templateUrl: './academic-unit-delete-dialog.component.html'
})
export class AcademicUnitDeleteDialogComponent {

    academicUnit: AcademicUnit;

    constructor(
        private academicUnitService: AcademicUnitService,
        public activeModal: NgbActiveModal,
        private eventManager: JhiEventManager
    ) {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.academicUnitService.delete(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: 'academicUnitListModification',
                content: 'Deleted an academicUnit'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-academic-unit-delete-popup',
    template: ''
})
export class AcademicUnitDeletePopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private academicUnitPopupService: AcademicUnitPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.academicUnitPopupService
                .open(AcademicUnitDeleteDialogComponent as Component, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
