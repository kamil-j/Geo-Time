import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs/Subscription';
import { JhiEventManager } from 'ng-jhipster';
import { ScheduleUnit } from '../../shared';
import { ScheduleUnitService } from './schedule-unit.service';

@Component({
    selector: 'jhi-schedule-unit-detail',
    templateUrl: './schedule-unit-detail.component.html'
})
export class ScheduleUnitDetailComponent implements OnInit, OnDestroy {

    scheduleUnit: ScheduleUnit;
    private subscription: Subscription;
    private eventSubscriber: Subscription;

    constructor(
        private eventManager: JhiEventManager,
        private scheduleUnitService: ScheduleUnitService,
        private route: ActivatedRoute
    ) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInScheduleUnits();
    }

    load(id) {
        this.scheduleUnitService.find(id)
            .subscribe((scheduleUnitResponse: HttpResponse<ScheduleUnit>) => {
                this.scheduleUnit = scheduleUnitResponse.body;
            });
    }
    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    registerChangeInScheduleUnits() {
        this.eventSubscriber = this.eventManager.subscribe(
            'scheduleUnitListModification',
            (response) => this.load(this.scheduleUnit.id)
        );
    }
}
