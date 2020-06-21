import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import {JhiAlertService, JhiEventManager} from 'ng-jhipster';
import { UserModalService } from './user-modal.service';
import { JhiLanguageHelper, User, UserService, UserGroup, Subdepartment } from '../../shared';
import {HttpErrorResponse, HttpResponse} from '@angular/common/http';
import { UserGroupService} from '../../entities/user-group';
import { SubdepartmentService} from '../../entities/subdepartment';

@Component({
    selector: 'jhi-user-mgmt-dialog',
    templateUrl: './user-management-dialog.component.html'
})
export class UserMgmtDialogComponent implements OnInit {

    user: User;
    subdepartments: Subdepartment[];
    usergroups: UserGroup[];
    languages: any[];
    authorities: any[];
    isSaving: Boolean;

    constructor(
        public activeModal: NgbActiveModal,
        private languageHelper: JhiLanguageHelper,
        private jhiAlertService: JhiAlertService,
        private subdepartmentService: SubdepartmentService,
        private userGroupService: UserGroupService,
        private userService: UserService,
        private eventManager: JhiEventManager
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.authorities = [];
        this.subdepartmentService.query()
            .subscribe((res: HttpResponse<Subdepartment[]>) => { this.subdepartments = res.body; }, (res: HttpErrorResponse) => this.onError(res.message));
        this.userGroupService.query()
            .subscribe((res: HttpResponse<UserGroup[]>) => { this.usergroups = res.body; }, (res: HttpErrorResponse) => this.onError(res.message));
        this.userService.authorities().subscribe((authorities) => {
            this.authorities = authorities;
        });
        this.languageHelper.getAll().then((languages) => {
            this.languages = languages;
        });
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        if (this.user.id !== null) {
            this.userService.update(this.user).subscribe((response) => this.onSaveSuccess(response), () => this.onSaveError());
        } else {
            this.userService.create(this.user).subscribe((response) => this.onSaveSuccess(response), () => this.onSaveError());
        }
    }

    private onSaveSuccess(result) {
        this.eventManager.broadcast({ name: 'userListModification', content: 'OK' });
        this.isSaving = false;
        this.activeModal.dismiss(result.body);
    }

    private onSaveError() {
        this.isSaving = false;
    }

    private onError(error: any) {
        this.jhiAlertService.error(error.message, null, null);
    }

    trackSubdepartmentById(index: number, item: Subdepartment) {
        return item.id;
    }

    trackUserGroupById(index: number, item: UserGroup) {
        return item.id;
    }
}

@Component({
    selector: 'jhi-user-dialog',
    template: ''
})
export class UserDialogComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private userModalService: UserModalService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['login'] ) {
                this.userModalService.open(UserMgmtDialogComponent as Component, params['login']);
            } else {
                this.userModalService.open(UserMgmtDialogComponent as Component);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
