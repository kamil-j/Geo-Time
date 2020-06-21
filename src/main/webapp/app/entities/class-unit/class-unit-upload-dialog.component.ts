import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';

import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {JhiAlertService, JhiEventManager} from 'ng-jhipster';
import {ClassUnitPopupService} from './class-unit-popup.service';
import {HttpErrorResponse, HttpResponse} from '@angular/common/http';
import {ClassUnitService} from './class-unit.service';
import {Subdepartment} from '../../shared';
import {SubdepartmentService} from '../subdepartment';

@Component({
    selector: 'jhi-class-unit-upload-dialog',
    templateUrl: './class-unit-upload-dialog.component.html'
})
export class ClassUnitUploadDialogComponent implements OnInit {

    subdepartments: Subdepartment[];
    subdepartmentId: number;
    file: File;
    isUploading: Boolean;

    constructor(
        public activeModal: NgbActiveModal,
        private jhiAlertService: JhiAlertService,
        private classUnitService: ClassUnitService,
        private subdepartmentService: SubdepartmentService,
        private eventManager: JhiEventManager
    ) {
    }

    ngOnInit() {
        this.isUploading = false;
        this.subdepartmentService.query()
            .subscribe((res: HttpResponse<Subdepartment[]>) => { this.subdepartments = res.body; },
                (res: HttpErrorResponse) => this.jhiAlertService.error(res.message, null, null));
    }

    uploadFile() {
        this.isUploading = true;
        this.classUnitService.uploadFile(this.file, this.subdepartmentId).subscribe(
            (res: HttpResponse<any>) => this.onSuccess(),
            (res: HttpErrorResponse) => this.onError()
        );
    }

    selectFile(event) {
        if (event.target.files && event.target.files.length) {
            this.file = event.target.files[0];
        }
    }

    trackSubdepartmentById(index: number, item: Subdepartment) {
        return item.id;
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    private onSuccess() {
        this.eventManager.broadcast({ name: 'classUnitListModification', content: 'OK' });
        this.activeModal.dismiss(true);
        this.isUploading = false;
    }

    private onError() {
        this.isUploading = false;
        this.activeModal.dismiss(true);
        this.jhiAlertService.error('geoTimeApp.classUnit.upload.error', null, null);
    }
}

@Component({
    selector: 'jhi-class-unit-upload-popup',
    template: ''
})
export class ClassUnitUploadPopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private classUnitPopupService: ClassUnitPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.classUnitPopupService
                .open(ClassUnitUploadDialogComponent as Component);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
