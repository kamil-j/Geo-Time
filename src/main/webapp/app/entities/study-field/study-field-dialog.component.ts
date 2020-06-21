import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';
import { StudyField } from '../../shared';
import { StudyFieldPopupService } from './study-field-popup.service';
import { StudyFieldService } from './study-field.service';

@Component({
    selector: 'jhi-study-field-dialog',
    templateUrl: './study-field-dialog.component.html'
})
export class StudyFieldDialogComponent implements OnInit {

    studyField: StudyField;
    isSaving: boolean;

    constructor(
        public activeModal: NgbActiveModal,
        private studyFieldService: StudyFieldService,
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
        if (this.studyField.id !== undefined) {
            this.subscribeToSaveResponse(
                this.studyFieldService.update(this.studyField));
        } else {
            this.subscribeToSaveResponse(
                this.studyFieldService.create(this.studyField));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<StudyField>>) {
        result.subscribe((res: HttpResponse<StudyField>) =>
            this.onSaveSuccess(res.body), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess(result: StudyField) {
        this.eventManager.broadcast({ name: 'studyFieldListModification', content: 'OK'});
        this.isSaving = false;
        this.activeModal.dismiss(result);
    }

    private onSaveError() {
        this.isSaving = false;
    }
}

@Component({
    selector: 'jhi-study-field-popup',
    template: ''
})
export class StudyFieldPopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private studyFieldPopupService: StudyFieldPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.studyFieldPopupService
                    .open(StudyFieldDialogComponent as Component, params['id']);
            } else {
                this.studyFieldPopupService
                    .open(StudyFieldDialogComponent as Component);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
