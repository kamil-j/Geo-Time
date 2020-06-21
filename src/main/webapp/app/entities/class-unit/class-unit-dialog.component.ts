import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';
import { ClassUnitPopupService } from './class-unit-popup.service';
import { ClassUnitService } from './class-unit.service';
import { ClassTypeService } from '../class-type';
import {
    User,
    UserService,
    ClassUnit,
    ClassType,
    AcademicUnit,
    Room,
    Semester,
    ClassUnitGroup,
    Subdepartment
} from '../../shared';
import { RoomService } from '../room';
import { SemesterService } from '../semester';
import { AcademicUnitService } from '../academic-unit';
import { ClassUnitGroupService } from '../class-unit-group';
import {SubdepartmentService} from '../subdepartment';

@Component({
    selector: 'jhi-class-unit-dialog',
    templateUrl: './class-unit-dialog.component.html'
})
export class ClassUnitDialogComponent implements OnInit {

    classUnit: ClassUnit;
    isSaving: boolean;
    assignSelectionType: string;

    classtypes: ClassType[];
    users: User[];
    rooms: Room[];
    semesters: Semester[];
    academicunits: AcademicUnit[];
    classunitgroups: ClassUnitGroup[];
    subdepartments: Subdepartment[];

    constructor(
        public activeModal: NgbActiveModal,
        private jhiAlertService: JhiAlertService,
        private classUnitService: ClassUnitService,
        private classTypeService: ClassTypeService,
        private userService: UserService,
        private roomService: RoomService,
        private semesterService: SemesterService,
        private academicUnitService: AcademicUnitService,
        private classUnitGroupService: ClassUnitGroupService,
        private subdepartmentService: SubdepartmentService,
        private eventManager: JhiEventManager
    ) {
    }

    ngOnInit() {
        this.isSaving = false;
        this.assignSelectionType = this.classUnit.userId != null ? 'user' : 'subdepartment';

        this.classTypeService.query()
            .subscribe((res: HttpResponse<ClassType[]>) => { this.classtypes = res.body; }, (res: HttpErrorResponse) => this.onError(res.message));
        this.userService.query({size: 1000})
            .subscribe((res: HttpResponse<User[]>) => { this.users = res.body; }, (res: HttpErrorResponse) => this.onError(res.message));
        this.roomService.query({size: 1000})
            .subscribe((res: HttpResponse<Room[]>) => { this.rooms = res.body; }, (res: HttpErrorResponse) => this.onError(res.message));
        this.semesterService.query()
            .subscribe((res: HttpResponse<Semester[]>) => { this.semesters = res.body; }, (res: HttpErrorResponse) => this.onError(res.message));
        this.academicUnitService.query({size: 1000})
            .subscribe((res: HttpResponse<AcademicUnit[]>) => { this.academicunits = res.body; }, (res: HttpErrorResponse) => this.onError(res.message));
        this.classUnitGroupService.query({size: 1000})
            .subscribe((res: HttpResponse<ClassUnitGroup[]>) => { this.classunitgroups = res.body; }, (res: HttpErrorResponse) => this.onError(res.message));
        this.subdepartmentService.query()
            .subscribe((res: HttpResponse<Subdepartment[]>) => { this.subdepartments = res.body; }, (res: HttpErrorResponse) => this.onError(res.message));
    }

    clearValues() {
        this.classUnit.userId = null;
        this.classUnit.subdepartmentId = null;
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        if (this.classUnit.id !== undefined) {
            this.subscribeToSaveResponse(
                this.classUnitService.update(this.classUnit));
        } else {
            this.subscribeToSaveResponse(
                this.classUnitService.create(this.classUnit));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<ClassUnit>>) {
        result.subscribe((res: HttpResponse<ClassUnit>) =>
            this.onSaveSuccess(res.body), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess(result: ClassUnit) {
        this.eventManager.broadcast({ name: 'classUnitListModification', content: 'OK'});
        this.isSaving = false;
        this.activeModal.dismiss(result);
    }

    private onSaveError() {
        this.isSaving = false;
    }

    private onError(error: any) {
        this.jhiAlertService.error(error.message, null, null);
    }

    trackClassTypeById(index: number, item: ClassType) {
        return item.id;
    }

    trackUserById(index: number, item: User) {
        return item.id;
    }

    trackRoomById(index: number, item: Room) {
        return item.id;
    }

    trackSemesterById(index: number, item: Semester) {
        return item.id;
    }

    trackAcademicUnitById(index: number, item: AcademicUnit) {
        return item.id;
    }

    trackClassUnitGroupById(index: number, item: ClassUnitGroup) {
        return item.id;
    }

    trackSubdepartmentById(index: number, item: Subdepartment) {
        return item.id;
    }

    getSelected(selectedVals: Array<any>, option: any) {
        if (selectedVals) {
            for (let i = 0; i < selectedVals.length; i++) {
                if (option.id === selectedVals[i].id) {
                    return selectedVals[i];
                }
            }
        }
        return option;
    }
}

@Component({
    selector: 'jhi-class-unit-popup',
    template: ''
})
export class ClassUnitPopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private classUnitPopupService: ClassUnitPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.classUnitPopupService
                    .open(ClassUnitDialogComponent as Component, params['id']);
            } else {
                this.classUnitPopupService
                    .open(ClassUnitDialogComponent as Component);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
