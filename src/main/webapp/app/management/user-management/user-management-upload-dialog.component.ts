import {Component, OnDestroy, OnInit} from '@angular/core';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {ActivatedRoute} from '@angular/router';
import {UserModalService} from './user-modal.service';
import {UserService} from '../../shared';
import {HttpErrorResponse, HttpResponse} from '@angular/common/http';
import {JhiAlertService, JhiEventManager} from 'ng-jhipster';

@Component({
    selector: 'jhi-user-mgmt-upload-dialog',
    templateUrl: './user-management-upload-dialog.component.html'
})
export class UserMgmtUploadDialogComponent implements OnInit {

    file: File;
    isUploading: Boolean;

    constructor(
        public activeModal: NgbActiveModal,
        private jhiAlertService: JhiAlertService,
        private userService: UserService,
        private eventManager: JhiEventManager
    ) {
    }

    ngOnInit() {
        this.isUploading = false;
    }

    uploadFile() {
        this.isUploading = true;
        this.userService.uploadFile(this.file).subscribe(
            (res: HttpResponse<any>) => this.onSuccess(),
            (res: HttpErrorResponse) => this.onError()
        );
    }

    selectFile(event) {
        if (event.target.files && event.target.files.length) {
            this.file = event.target.files[0];
        }
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    private onSuccess() {
        this.eventManager.broadcast({ name: 'userListModification', content: 'OK' });
        this.activeModal.dismiss(true);
        this.isUploading = false;
    }

    private onError() {
        this.isUploading = false;
        this.activeModal.dismiss(true);
        this.jhiAlertService.error('userManagement.upload.error', null, null);
    }
}

@Component({
    selector: 'jhi-user-upload-dialog',
    template: ''
})
export class UserUploadDialogComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private userModalService: UserModalService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe(() => {
            this.userModalService.open(UserMgmtUploadDialogComponent as Component);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
