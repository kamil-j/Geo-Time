import {Component, Input, OnInit, ViewChild} from '@angular/core';

import {HttpErrorResponse, HttpResponse} from '@angular/common/http';
import {JhiAlertService} from 'ng-jhipster';
import {ClassType, ClassUnitCreate, Semester} from '../../../../shared';
import {ClassTypeService} from '../../../class-type';
import {SemesterService} from '../../../semester';
import {NgForm} from '@angular/forms';

@Component({
    selector: 'jhi-class-unit-create-step1',
    templateUrl: './class-unit-create-step1.component.html'
})
export class ClassUnitCreateStep1Component implements OnInit {

    @Input() classUnit: ClassUnitCreate;
    @ViewChild('form') form: NgForm;

    classTypes: ClassType[];
    semesters: Semester[];

    constructor(
        private jhiAlertService: JhiAlertService,
        private classTypeService: ClassTypeService,
        private semesterService: SemesterService
    ) {
    }

    ngOnInit() {
        this.load();
    }

    load() {
        this.classTypeService.query()
            .subscribe((res: HttpResponse<ClassType[]>) => { this.classTypes = res.body; }, (res: HttpErrorResponse) => this.onError(res.message));
        this.semesterService.query()
            .subscribe((res: HttpResponse<Semester[]>) => { this.semesters = res.body; }, (res: HttpErrorResponse) => this.onError(res.message));
    }

    private onError(error: any) {
        this.jhiAlertService.error(error.message, null, null);
    }

    trackClassTypeById(index: number, item: ClassType) {
        return item.id;
    }

    trackSemesterById(index: number, item: Semester) {
        return item.id;
    }
}
