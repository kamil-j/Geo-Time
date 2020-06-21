import {Component, OnInit, ViewChild} from '@angular/core';
import {ClassUnit, ClassUnitCreate} from '../../../shared';
import {ClassUnitCreateStep1Component} from './step1/class-unit-create-step1.component';
import {ClassUnitCreateStep2Component} from './step2/class-unit-create-step2.component';
import {ClassUnitCreateStep3Component} from './step3/class-unit-create-step3.component';
import {ClassUnitCreateStep4Component} from './step4/class-unit-create-step4.component';
import {ClassUnitService} from '../class-unit.service';
import {HttpErrorResponse, HttpResponse} from '@angular/common/http';
import {JhiAlertService, JhiEventManager} from 'ng-jhipster';
import {Router} from '@angular/router';

@Component({
    selector: 'jhi-class-unit-create',
    templateUrl: './class-unit-create.component.html'
})
export class ClassUnitCreateComponent implements OnInit {

    classUnit: ClassUnitCreate;

    @ViewChild('step1') step1: ClassUnitCreateStep1Component;
    @ViewChild('step2') step2: ClassUnitCreateStep2Component;
    @ViewChild('step3') step3: ClassUnitCreateStep3Component;
    @ViewChild('step3') step4: ClassUnitCreateStep4Component;

    constructor(
        private classUnitService: ClassUnitService,
        private eventManager: JhiEventManager,
        private router: Router,
        private jhiAlertService: JhiAlertService
    ) {
    }

    ngOnInit() {
        this.classUnit = new ClassUnitCreate();
    }

    onComplete() {
        this.classUnitService.create(this.classUnit).subscribe((
            res: HttpResponse<ClassUnit>) => this.onSuccess(),
            (res: HttpErrorResponse) => this.onError(res.message)
        );
    }

    private onSuccess() {
        this.eventManager.broadcast({ name: 'classUnitListModification', content: 'OK'});
        this.router.navigate(['/class-unit']);
    }

    private onError(error: any) {
        this.jhiAlertService.error(error.message, null, null);
    }
}
