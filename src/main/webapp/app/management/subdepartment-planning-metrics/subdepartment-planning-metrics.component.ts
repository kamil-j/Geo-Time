import {Component, OnDestroy, OnInit} from '@angular/core';
import {HttpErrorResponse, HttpResponse} from '@angular/common/http';
import {JhiAlertService} from 'ng-jhipster';
import {SubdepartmentPlanningMetricsService} from './subdepartment-planning-metrics.service';
import {SubdepartmentPlanningMetrics} from './subdepartment-planning-metrics.model';
import {Subscription} from 'rxjs';
import {ActivatedRoute} from '@angular/router';

@Component({
    selector: 'jhi-subdepartment-planning-metrics',
    templateUrl: './subdepartment-planning-metrics.component.html'
})
export class SubdepartmentPlanningMetricsComponent implements OnInit, OnDestroy {

    arePlanningMetricsLoading = true;
    planningMetrics: SubdepartmentPlanningMetrics;
    predicate = 'true';
    reverse = false;
    private subdepartmentId: number;
    private subscription: Subscription;

    constructor(
        private planningMetricsService: SubdepartmentPlanningMetricsService,
        private jhiAlertService: JhiAlertService,
        private route: ActivatedRoute
    ) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.subdepartmentId = params['id'];
        });
        this.refresh();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    refresh() {
        this.arePlanningMetricsLoading = true;
        this.planningMetricsService.getPlanningMetrics(this.subdepartmentId)
            .subscribe((res: HttpResponse<SubdepartmentPlanningMetrics>) => {
                    this.planningMetrics = res.body;
                    this.arePlanningMetricsLoading = false;
                },
                (res: HttpErrorResponse) => this.onError(res.message));
    }

    private onError(error: any) {
        this.jhiAlertService.error(error.message, null, null);
    }

    previousState() {
        window.history.back();
    }

    sortUsersMetrics() {
        this.planningMetrics.usersPlanningMetrics.sort((o1, o2) => {
            if (this.predicate === 'userName') {
                if (this.reverse) {
                    return o1.userName < o2.userName ? -1 : 1;
                }
                return o1.userName > o2.userName ? -1 : 1;
            } else if (this.predicate === 'groupName') {
                if (this.reverse) {
                    return o1.groupName < o2.groupName ? -1 : 1;
                }
                return o1.groupName > o2.groupName ? -1 : 1;
            } else if (this.predicate === 'planningProgress') {
                if (this.reverse) {
                    return o1.bookedClassQuantity / o1.classQuantity < o2.bookedClassQuantity / o2.classQuantity ? -1 : 1;
                }
                return o1.bookedClassQuantity / o1.classQuantity > o2.bookedClassQuantity / o2.classQuantity ? -1 : 1;
            }
            return 0;
        });
    }
}
