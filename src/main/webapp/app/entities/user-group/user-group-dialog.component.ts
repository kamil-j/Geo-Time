import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';
import { UserGroup } from '../../shared';
import { UserGroupPopupService } from './user-group-popup.service';
import { UserGroupService } from './user-group.service';

@Component({
    selector: 'jhi-user-group-dialog',
    templateUrl: './user-group-dialog.component.html'
})
export class UserGroupDialogComponent implements OnInit {

    userGroup: UserGroup;
    isSaving: boolean;

    constructor(
        public activeModal: NgbActiveModal,
        private userGroupService: UserGroupService,
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
        if (this.userGroup.id !== undefined) {
            this.subscribeToSaveResponse(
                this.userGroupService.update(this.userGroup));
        } else {
            this.subscribeToSaveResponse(
                this.userGroupService.create(this.userGroup));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<UserGroup>>) {
        result.subscribe((res: HttpResponse<UserGroup>) =>
            this.onSaveSuccess(res.body), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess(result: UserGroup) {
        this.eventManager.broadcast({ name: 'userGroupListModification', content: 'OK'});
        this.isSaving = false;
        this.activeModal.dismiss(result);
    }

    private onSaveError() {
        this.isSaving = false;
    }
}

@Component({
    selector: 'jhi-user-group-popup',
    template: ''
})
export class UserGroupPopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private userGroupPopupService: UserGroupPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.userGroupPopupService
                    .open(UserGroupDialogComponent as Component, params['id']);
            } else {
                this.userGroupPopupService
                    .open(UserGroupDialogComponent as Component);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
