import {Component, Input, ViewChild} from '@angular/core';

import {HttpErrorResponse, HttpResponse} from '@angular/common/http';
import {JhiAlertService} from 'ng-jhipster';
import {AcademicUnit, ClassUnitCreate} from '../../../../shared';
import {NgForm} from '@angular/forms';
import {AcademicUnitService} from '../../../academic-unit';

@Component({
    selector: 'jhi-class-unit-create-step2',
    templateUrl: './class-unit-create-step2.component.html',
    styleUrls: [
        'class-unit-create-step2.css'
    ],
})
export class ClassUnitCreateStep2Component {

    @Input() classUnit: ClassUnitCreate;
    @ViewChild('form') form: NgForm;

    selectAcademicUnitsData: any[];

    constructor(
        private jhiAlertService: JhiAlertService,
        private academicUnitService: AcademicUnitService,
    ) {
        this.selectAcademicUnitsData = [];
    }

    load() {
        this.academicUnitService.query({size: 1000}).subscribe(
            (res: HttpResponse<AcademicUnit[]>) => this.onSuccess(res),
            (res: HttpErrorResponse) => this.onError(res.message));
    }

    private onSuccess(response) {
        this.selectAcademicUnitsData = response.body.map((academicUnit) => {
            return {
                id: academicUnit.id.toString(),
                text: academicUnit.name
            };
        });
    }

    private onError(error: any) {
        this.jhiAlertService.error(error.message, null, null);
    }
}
