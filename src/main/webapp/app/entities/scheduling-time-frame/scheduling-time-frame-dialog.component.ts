import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';
import { SchedulingTimeFrame, UserGroup, Subdepartment, Semester } from '../../shared';
import { SchedulingTimeFramePopupService } from './scheduling-time-frame-popup.service';
import { SchedulingTimeFrameService } from './scheduling-time-frame.service';
import { UserGroupService } from '../user-group';
import { SubdepartmentService } from '../subdepartment';
import { SemesterService } from '../semester';

@Component({
    selector: 'jhi-scheduling-time-frame-dialog',
    templateUrl: './scheduling-time-frame-dialog.component.html'
})
export class SchedulingTimeFrameDialogComponent implements OnInit {

    schedulingTimeFrame: SchedulingTimeFrame;
    isSaving: boolean;

    usergroups: UserGroup[];

    subdepartments: Subdepartment[];

    semesters: Semester[];

    constructor(
        public activeModal: NgbActiveModal,
        private jhiAlertService: JhiAlertService,
        private schedulingTimeFrameService: SchedulingTimeFrameService,
        private userGroupService: UserGroupService,
        private subdepartmentService: SubdepartmentService,
        private semesterService: SemesterService,
        private eventManager: JhiEventManager
    ) {
    }

    ngOnInit() {
        this.isSaving = false;
        this.userGroupService.query()
            .subscribe((res: HttpResponse<UserGroup[]>) => { this.usergroups = res.body; }, (res: HttpErrorResponse) => this.onError(res.message));
        this.subdepartmentService.query()
            .subscribe((res: HttpResponse<Subdepartment[]>) => { this.subdepartments = res.body; }, (res: HttpErrorResponse) => this.onError(res.message));
        this.semesterService.query()
            .subscribe((res: HttpResponse<Semester[]>) => { this.semesters = res.body; }, (res: HttpErrorResponse) => this.onError(res.message));
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        if (this.schedulingTimeFrame.id !== undefined) {
            this.subscribeToSaveResponse(
                this.schedulingTimeFrameService.update(this.schedulingTimeFrame));
        } else {
            this.subscribeToSaveResponse(
                this.schedulingTimeFrameService.create(this.schedulingTimeFrame));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<SchedulingTimeFrame>>) {
        result.subscribe((res: HttpResponse<SchedulingTimeFrame>) =>
            this.onSaveSuccess(res.body), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess(result: SchedulingTimeFrame) {
        this.eventManager.broadcast({ name: 'schedulingTimeFrameListModification', content: 'OK'});
        this.isSaving = false;
        this.activeModal.dismiss(result);
    }

    private onSaveError() {
        this.isSaving = false;
    }

    private onError(error: any) {
        this.jhiAlertService.error(error.message, null, null);
    }

    trackUserGroupById(index: number, item: UserGroup) {
        return item.id;
    }

    trackSubdepartmentById(index: number, item: Subdepartment) {
        return item.id;
    }

    trackSemesterById(index: number, item: Semester) {
        return item.id;
    }
}

@Component({
    selector: 'jhi-scheduling-time-frame-popup',
    template: ''
})
export class SchedulingTimeFramePopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private schedulingTimeFramePopupService: SchedulingTimeFramePopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.schedulingTimeFramePopupService
                    .open(SchedulingTimeFrameDialogComponent as Component, params['id']);
            } else {
                this.schedulingTimeFramePopupService
                    .open(SchedulingTimeFrameDialogComponent as Component);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
