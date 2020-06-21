import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';
import { Subdepartment } from '../../shared';
import { SubdepartmentPopupService } from './subdepartment-popup.service';
import { SubdepartmentService } from './subdepartment.service';

@Component({
    selector: 'jhi-subdepartment-dialog',
    templateUrl: './subdepartment-dialog.component.html'
})
export class SubdepartmentDialogComponent implements OnInit {

    subdepartment: Subdepartment;
    isSaving: boolean;

    constructor(
        public activeModal: NgbActiveModal,
        private subdepartmentService: SubdepartmentService,
        private eventManager: JhiEventManager
    ) {
    }

    ngOnInit() {
        this.isSaving = false;
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        if (this.subdepartment.id !== undefined) {
            this.subscribeToSaveResponse(
                this.subdepartmentService.update(this.subdepartment));
        } else {
            this.subscribeToSaveResponse(
                this.subdepartmentService.create(this.subdepartment));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<Subdepartment>>) {
        result.subscribe((res: HttpResponse<Subdepartment>) =>
            this.onSaveSuccess(res.body), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess(result: Subdepartment) {
        this.eventManager.broadcast({ name: 'subdepartmentListModification', content: 'OK'});
        this.isSaving = false;
        this.activeModal.dismiss(result);
    }

    private onSaveError() {
        this.isSaving = false;
    }
}

@Component({
    selector: 'jhi-subdepartment-popup',
    template: ''
})
export class SubdepartmentPopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private subdepartmentPopupService: SubdepartmentPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.subdepartmentPopupService
                    .open(SubdepartmentDialogComponent as Component, params['id']);
            } else {
                this.subdepartmentPopupService
                    .open(SubdepartmentDialogComponent as Component);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
