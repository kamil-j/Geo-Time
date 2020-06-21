import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {HttpErrorResponse, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {JhiAlertService, JhiEventManager} from 'ng-jhipster';
import {ClassUnitGroup} from '../../shared';
import {ClassUnitGroupPopupService} from './class-unit-group-popup.service';
import {ClassUnitGroupService} from './class-unit-group.service';
import {AccountService} from '../../shared';

@Component({
    selector: 'jhi-class-unit-group-dialog',
    templateUrl: './class-unit-group-dialog.component.html'
})
export class ClassUnitGroupDialogComponent implements OnInit {

    classUnitGroup: ClassUnitGroup;
    isSaving: boolean;

    constructor(
        public activeModal: NgbActiveModal,
        private jhiAlertService: JhiAlertService,
        private classUnitGroupService: ClassUnitGroupService,
        private accountService: AccountService,
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
        if (this.classUnitGroup.id !== undefined) {
            this.subscribeToSaveResponse(
                this.classUnitGroupService.update(this.classUnitGroup));
        } else {
            this.subscribeToSaveResponse(
                this.classUnitGroupService.create(this.classUnitGroup));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<ClassUnitGroup>>) {
        result.subscribe((res: HttpResponse<ClassUnitGroup>) =>
            this.onSaveSuccess(res.body), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess(result: ClassUnitGroup) {
        this.eventManager.broadcast({ name: 'classUnitGroupListModification', content: 'OK'});
        this.isSaving = false;
        this.activeModal.dismiss(result);
    }

    private onSaveError() {
        this.isSaving = false;
    }
}

@Component({
    selector: 'jhi-class-unit-group-popup',
    template: ''
})
export class ClassUnitGroupPopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private classUnitGroupPopupService: ClassUnitGroupPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.classUnitGroupPopupService
                    .open(ClassUnitGroupDialogComponent as Component, params['id']);
            } else {
                this.classUnitGroupPopupService
                    .open(ClassUnitGroupDialogComponent as Component);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
