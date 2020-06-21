import {Component, OnInit} from '@angular/core';
import {HttpErrorResponse, HttpResponse} from '@angular/common/http';
import {JhiAlertService} from 'ng-jhipster';
import {DepartmentPlanningMetrics} from './department-planning-metrics.model';
import {DepartmentPlanningMetricsService} from './department-planning-metrics.service';

@Component({
    selector: 'jhi-department-planning-metrics',
    templateUrl: './department-planning-metrics.component.html'
})
export class DepartmentPlanningMetricsComponent implements OnInit {

    arePlanningMetricsLoading = true;
    planningMetrics: DepartmentPlanningMetrics;

    constructor(
        private planningMetricsService: DepartmentPlanningMetricsService,
        private jhiAlertService: JhiAlertService,
    ) {
    }

    ngOnInit() {
        this.refresh();
    }

    refresh() {
        this.arePlanningMetricsLoading = true;
        this.planningMetricsService.getDepartmentPlanningMetrics()
            .subscribe((res: HttpResponse<DepartmentPlanningMetrics>) => {
                    this.planningMetrics = res.body;
                    this.arePlanningMetricsLoading = false;
                },
                (res: HttpErrorResponse) => this.onError(res.message));
    }

    private onError(error: any) {
        this.jhiAlertService.error(error.message, null, null);
    }
}
