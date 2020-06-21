import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';
import { ClassUnitGroup } from '../../shared';
import { ClassUnitGroupPopupService } from './class-unit-group-popup.service';
import { ClassUnitGroupService } from './class-unit-group.service';

@Component({
    selector: 'jhi-class-unit-group-delete-dialog',
    templateUrl: './class-unit-group-delete-dialog.component.html'
})
export class ClassUnitGroupDeleteDialogComponent {

    classUnitGroup: ClassUnitGroup;

    constructor(
        private classUnitGroupService: ClassUnitGroupService,
        public activeModal: NgbActiveModal,
        private eventManager: JhiEventManager
    ) {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.classUnitGroupService.delete(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: 'classUnitGroupListModification',
                content: 'Deleted an classUnitGroup'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-class-unit-group-delete-popup',
    template: ''
})
export class ClassUnitGroupDeletePopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private classUnitGroupPopupService: ClassUnitGroupPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.classUnitGroupPopupService
                .open(ClassUnitGroupDeleteDialogComponent as Component, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
