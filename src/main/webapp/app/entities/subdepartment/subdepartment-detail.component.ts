import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs/Subscription';
import { JhiEventManager } from 'ng-jhipster';
import { Subdepartment } from '../../shared';
import { SubdepartmentService } from './subdepartment.service';

@Component({
    selector: 'jhi-subdepartment-detail',
    templateUrl: './subdepartment-detail.component.html'
})
export class SubdepartmentDetailComponent implements OnInit, OnDestroy {

    subdepartment: Subdepartment;
    private subscription: Subscription;
    private eventSubscriber: Subscription;

    constructor(
        private eventManager: JhiEventManager,
        private subdepartmentService: SubdepartmentService,
        private route: ActivatedRoute
    ) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInSubdepartments();
    }

    load(id) {
        this.subdepartmentService.find(id)
            .subscribe((subdepartmentResponse: HttpResponse<Subdepartment>) => {
                this.subdepartment = subdepartmentResponse.body;
            });
    }
    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    registerChangeInSubdepartments() {
        this.eventSubscriber = this.eventManager.subscribe(
            'subdepartmentListModification',
            (response) => this.load(this.subdepartment.id)
        );
    }
}
