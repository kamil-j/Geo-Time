import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs/Subscription';
import { JhiEventManager } from 'ng-jhipster';
import { AcademicUnit } from '../../shared';
import { AcademicUnitService } from './academic-unit.service';

@Component({
    selector: 'jhi-academic-unit-detail',
    templateUrl: './academic-unit-detail.component.html'
})
export class AcademicUnitDetailComponent implements OnInit, OnDestroy {

    academicUnit: AcademicUnit;
    private subscription: Subscription;
    private eventSubscriber: Subscription;

    constructor(
        private eventManager: JhiEventManager,
        private academicUnitService: AcademicUnitService,
        private route: ActivatedRoute
    ) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInAcademicUnits();
    }

    load(id) {
        this.academicUnitService.find(id)
            .subscribe((academicUnitResponse: HttpResponse<AcademicUnit>) => {
                this.academicUnit = academicUnitResponse.body;
            });
    }
    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    registerChangeInAcademicUnits() {
        this.eventSubscriber = this.eventManager.subscribe(
            'academicUnitListModification',
            (response) => this.load(this.academicUnit.id)
        );
    }
}
