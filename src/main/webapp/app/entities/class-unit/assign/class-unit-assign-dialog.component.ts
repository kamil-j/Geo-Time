import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import {ClassUnitPopupService} from '../class-unit-popup.service';
import {ClassUnit, User, UserService} from '../../../shared';
import {HttpErrorResponse, HttpResponse} from '@angular/common/http';
import {JhiAlertService, JhiEventManager} from 'ng-jhipster';
import {ClassUnitService} from '../class-unit.service';

@Component({
    selector: 'jhi-class-unit-assign-dialog',
    templateUrl: './class-unit-assign-dialog.component.html'
})
export class ClassUnitAssignDialogComponent implements OnInit {

    classUnit: ClassUnit;
    isSaving: boolean;
    userId: number;
    selectUserData: any[];

    constructor(
        public activeModal: NgbActiveModal,
        private jhiAlertService: JhiAlertService,
        private classUnitService: ClassUnitService,
        private userService: UserService,
        private eventManager: JhiEventManager
    ) {
    }

    ngOnInit() {
        this.isSaving = false;
        this.userService.query({size: 1000})
            .subscribe((res: HttpResponse<User[]>) => this.onUserSuccess(res),
                (res: HttpErrorResponse) => this.onError(res.message));
    }

    private onError(error: any) {
        this.jhiAlertService.error(error.message, null, null);
    }

    private onUserSuccess(response) {
        this.selectUserData = response.body.map((user) => {
            return {
                id: user.id.toString(),
                text: user.lastName + ' ' + user.firstName
            };
        });
    }

    save() {
        this.isSaving = true;
        this.classUnitService.assign(this.classUnit.id, this.userId).subscribe(
            (res: HttpResponse<any>) => this.onSaveSuccess(),
            (res: HttpErrorResponse) => this.onSaveError()
        );
    }

    private onSaveSuccess() {
        this.eventManager.broadcast({ name: 'classUnitListModification', content: 'OK'});
        this.isSaving = false;
        this.activeModal.dismiss();
    }

    private onSaveError() {
        this.isSaving = false;
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }
}

@Component({
    selector: 'jhi-class-unit-assign-popup',
    template: ''
})
export class ClassUnitAssignPopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private classUnitPopupService: ClassUnitPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.classUnitPopupService
                .open(ClassUnitAssignDialogComponent as Component, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
