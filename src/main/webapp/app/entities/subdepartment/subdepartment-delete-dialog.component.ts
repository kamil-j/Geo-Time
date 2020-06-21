import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';
import { Subdepartment } from '../../shared';
import { SubdepartmentPopupService } from './subdepartment-popup.service';
import { SubdepartmentService } from './subdepartment.service';

@Component({
    selector: 'jhi-subdepartment-delete-dialog',
    templateUrl: './subdepartment-delete-dialog.component.html'
})
export class SubdepartmentDeleteDialogComponent {

    subdepartment: Subdepartment;

    constructor(
        private subdepartmentService: SubdepartmentService,
        public activeModal: NgbActiveModal,
        private eventManager: JhiEventManager
    ) {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.subdepartmentService.delete(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: 'subdepartmentListModification',
                content: 'Deleted an subdepartment'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-subdepartment-delete-popup',
    template: ''
})
export class SubdepartmentDeletePopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private subdepartmentPopupService: SubdepartmentPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.subdepartmentPopupService
                .open(SubdepartmentDeleteDialogComponent as Component, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
