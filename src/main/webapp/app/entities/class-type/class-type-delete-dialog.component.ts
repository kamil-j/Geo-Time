import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';
import { ClassType } from '../../shared';
import { ClassTypePopupService } from './class-type-popup.service';
import { ClassTypeService } from './class-type.service';

@Component({
    selector: 'jhi-class-type-delete-dialog',
    templateUrl: './class-type-delete-dialog.component.html'
})
export class ClassTypeDeleteDialogComponent {

    classType: ClassType;

    constructor(
        private classTypeService: ClassTypeService,
        public activeModal: NgbActiveModal,
        private eventManager: JhiEventManager
    ) {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.classTypeService.delete(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: 'classTypeListModification',
                content: 'Deleted an classType'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-class-type-delete-popup',
    template: ''
})
export class ClassTypeDeletePopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private classTypePopupService: ClassTypePopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.classTypePopupService
                .open(ClassTypeDeleteDialogComponent as Component, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
