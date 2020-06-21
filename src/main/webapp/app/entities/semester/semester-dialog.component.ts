import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';
import { Semester } from '../../shared';
import { SemesterPopupService } from './semester-popup.service';
import { SemesterService } from './semester.service';

@Component({
    selector: 'jhi-semester-dialog',
    templateUrl: './semester-dialog.component.html'
})
export class SemesterDialogComponent implements OnInit {

    semester: Semester;
    isSaving: boolean;

    constructor(
        public activeModal: NgbActiveModal,
        private semesterService: SemesterService,
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
        if (this.semester.id !== undefined) {
            this.subscribeToSaveResponse(
                this.semesterService.update(this.semester));
        } else {
            this.subscribeToSaveResponse(
                this.semesterService.create(this.semester));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<Semester>>) {
        result.subscribe((res: HttpResponse<Semester>) =>
            this.onSaveSuccess(res.body), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess(result: Semester) {
        this.eventManager.broadcast({ name: 'semesterListModification', content: 'OK'});
        this.isSaving = false;
        this.activeModal.dismiss(result);
    }

    private onSaveError() {
        this.isSaving = false;
    }
}

@Component({
    selector: 'jhi-semester-popup',
    template: ''
})
export class SemesterPopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private semesterPopupService: SemesterPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.semesterPopupService
                    .open(SemesterDialogComponent as Component, params['id']);
            } else {
                this.semesterPopupService
                    .open(SemesterDialogComponent as Component);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
