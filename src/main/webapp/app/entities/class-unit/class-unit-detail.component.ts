import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs/Subscription';
import { JhiEventManager } from 'ng-jhipster';
import { ClassUnit } from '../../shared';
import { ClassUnitService } from './class-unit.service';

@Component({
    selector: 'jhi-class-unit-detail',
    templateUrl: './class-unit-detail.component.html'
})
export class ClassUnitDetailComponent implements OnInit, OnDestroy {

    classUnit: ClassUnit;
    private subscription: Subscription;
    private eventSubscriber: Subscription;

    constructor(
        private eventManager: JhiEventManager,
        private classUnitService: ClassUnitService,
        private route: ActivatedRoute
    ) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInClassUnits();
    }

    load(id) {
        this.classUnitService.find(id)
            .subscribe((classUnitResponse: HttpResponse<ClassUnit>) => {
                this.classUnit = classUnitResponse.body;
            });
    }

    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    registerChangeInClassUnits() {
        this.eventSubscriber = this.eventManager.subscribe(
            'classUnitListModification',
            (response) => this.load(this.classUnit.id)
        );
    }
}
