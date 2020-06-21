import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs/Subscription';
import { JhiEventManager } from 'ng-jhipster';
import { SchedulingTimeFrame } from '../../shared';
import { SchedulingTimeFrameService } from './scheduling-time-frame.service';

@Component({
    selector: 'jhi-scheduling-time-frame-detail',
    templateUrl: './scheduling-time-frame-detail.component.html'
})
export class SchedulingTimeFrameDetailComponent implements OnInit, OnDestroy {

    schedulingTimeFrame: SchedulingTimeFrame;
    private subscription: Subscription;
    private eventSubscriber: Subscription;

    constructor(
        private eventManager: JhiEventManager,
        private schedulingTimeFrameService: SchedulingTimeFrameService,
        private route: ActivatedRoute
    ) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInSchedulingTimeFrames();
    }

    load(id) {
        this.schedulingTimeFrameService.find(id)
            .subscribe((schedulingTimeFrameResponse: HttpResponse<SchedulingTimeFrame>) => {
                this.schedulingTimeFrame = schedulingTimeFrameResponse.body;
            });
    }
    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    registerChangeInSchedulingTimeFrames() {
        this.eventSubscriber = this.eventManager.subscribe(
            'schedulingTimeFrameListModification',
            (response) => this.load(this.schedulingTimeFrame.id)
        );
    }
}
