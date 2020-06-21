import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';
import { ClassUnit } from '../../shared';
import { ClassUnitPopupService } from './class-unit-popup.service';
import { ClassUnitService } from './class-unit.service';

@Component({
    selector: 'jhi-class-unit-delete-dialog',
    templateUrl: './class-unit-delete-dialog.component.html'
})
export class ClassUnitDeleteDialogComponent {

    classUnit: ClassUnit;

    constructor(
        private classUnitService: ClassUnitService,
        public activeModal: NgbActiveModal,
        private eventManager: JhiEventManager
    ) {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.classUnitService.delete(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: 'classUnitListModification',
                content: 'Deleted an classUnit'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-class-unit-delete-popup',
    template: ''
})
export class ClassUnitDeletePopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private classUnitPopupService: ClassUnitPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.classUnitPopupService
                .open(ClassUnitDeleteDialogComponent as Component, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
