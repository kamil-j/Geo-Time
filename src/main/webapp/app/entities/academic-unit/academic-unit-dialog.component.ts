import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';
import { AcademicUnit, StudyField } from '../../shared';
import { AcademicUnitPopupService } from './academic-unit-popup.service';
import { AcademicUnitService } from './academic-unit.service';
import { StudyFieldService } from '../study-field';

@Component({
    selector: 'jhi-academic-unit-dialog',
    templateUrl: './academic-unit-dialog.component.html'
})
export class AcademicUnitDialogComponent implements OnInit {

    academicUnit: AcademicUnit;
    isSaving: boolean;

    studyfields: StudyField[];

    constructor(
        public activeModal: NgbActiveModal,
        private jhiAlertService: JhiAlertService,
        private academicUnitService: AcademicUnitService,
        private studyFieldService: StudyFieldService,
        private eventManager: JhiEventManager
    ) {
    }

    ngOnInit() {
        this.isSaving = false;
        this.studyFieldService.query({size: 1000})
            .subscribe((res: HttpResponse<StudyField[]>) => { this.studyfields = res.body; }, (res: HttpErrorResponse) => this.onError(res.message));
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        if (this.academicUnit.id !== undefined) {
            this.subscribeToSaveResponse(
                this.academicUnitService.update(this.academicUnit));
        } else {
            this.subscribeToSaveResponse(
                this.academicUnitService.create(this.academicUnit));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<AcademicUnit>>) {
        result.subscribe((res: HttpResponse<AcademicUnit>) =>
            this.onSaveSuccess(res.body), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess(result: AcademicUnit) {
        this.eventManager.broadcast({ name: 'academicUnitListModification', content: 'OK'});
        this.isSaving = false;
        this.activeModal.dismiss(result);
    }

    private onSaveError() {
        this.isSaving = false;
    }

    private onError(error: any) {
        this.jhiAlertService.error(error.message, null, null);
    }

    trackStudyFieldById(index: number, item: StudyField) {
        return item.id;
    }
}

@Component({
    selector: 'jhi-academic-unit-popup',
    template: ''
})
export class AcademicUnitPopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private academicUnitPopupService: AcademicUnitPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.academicUnitPopupService
                    .open(AcademicUnitDialogComponent as Component, params['id']);
            } else {
                this.academicUnitPopupService
                    .open(AcademicUnitDialogComponent as Component);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
