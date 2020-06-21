import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';
import { ClassType } from '../../shared';
import { ClassTypePopupService } from './class-type-popup.service';
import { ClassTypeService } from './class-type.service';

@Component({
    selector: 'jhi-class-type-dialog',
    templateUrl: './class-type-dialog.component.html'
})
export class ClassTypeDialogComponent implements OnInit {

    classType: ClassType;
    isSaving: boolean;

    constructor(
        public activeModal: NgbActiveModal,
        private classTypeService: ClassTypeService,
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
        if (this.classType.id !== undefined) {
            this.subscribeToSaveResponse(
                this.classTypeService.update(this.classType));
        } else {
            this.subscribeToSaveResponse(
                this.classTypeService.create(this.classType));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<ClassType>>) {
        result.subscribe((res: HttpResponse<ClassType>) =>
            this.onSaveSuccess(res.body), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess(result: ClassType) {
        this.eventManager.broadcast({ name: 'classTypeListModification', content: 'OK'});
        this.isSaving = false;
        this.activeModal.dismiss(result);
    }

    private onSaveError() {
        this.isSaving = false;
    }
}

@Component({
    selector: 'jhi-class-type-popup',
    template: ''
})
export class ClassTypePopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private classTypePopupService: ClassTypePopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.classTypePopupService
                    .open(ClassTypeDialogComponent as Component, params['id']);
            } else {
                this.classTypePopupService
                    .open(ClassTypeDialogComponent as Component);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
