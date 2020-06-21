import {Component, Input, ViewChild} from '@angular/core';

import {HttpErrorResponse, HttpResponse} from '@angular/common/http';
import {JhiAlertService} from 'ng-jhipster';
import {ClassUnitCreate, ClassUnitGroup} from '../../../../shared';
import {NgForm} from '@angular/forms';
import {ClassUnitGroupService} from '../../../class-unit-group';

@Component({
    selector: 'jhi-class-unit-create-step4',
    templateUrl: './class-unit-create-step4.component.html',
    styleUrls: [
        'class-unit-create-step4.css'
    ],
})
export class ClassUnitCreateStep4Component {

    @Input() classUnit: ClassUnitCreate;
    @ViewChild('form') form: NgForm;

    selectClassUnitGroupData: any[];
    classUnitGroupSelectionType = '';

    constructor(
        private jhiAlertService: JhiAlertService,
        private classUnitGroupService: ClassUnitGroupService
    ) {
    }

    clearValues() {
        this.classUnit.classUnitGroup.id = null;
        this.classUnit.classUnitGroup.name = null;
    }

    load() {
        this.classUnitGroupService.query({size: 1000})
            .subscribe((res: HttpResponse<ClassUnitGroup[]>) => this.onClassUnitGroupSuccess(res),
                (res: HttpErrorResponse) => this.onError(res.message));
    }

    private onError(error: any) {
        this.jhiAlertService.error(error.message, null, null);
    }

    private onClassUnitGroupSuccess(response) {
        this.selectClassUnitGroupData = response.body.map((classUnitGroup) => {
            return {
                id: classUnitGroup.id.toString(),
                text: classUnitGroup.name
            };
        });
    }
}
