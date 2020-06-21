import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs/Subscription';
import { JhiEventManager } from 'ng-jhipster';
import { ClassUnitGroup } from '../../shared';
import { ClassUnitGroupService } from './class-unit-group.service';

@Component({
    selector: 'jhi-class-unit-group-detail',
    templateUrl: './class-unit-group-detail.component.html'
})
export class ClassUnitGroupDetailComponent implements OnInit, OnDestroy {

    classUnitGroup: ClassUnitGroup;
    private subscription: Subscription;
    private eventSubscriber: Subscription;

    constructor(
        private eventManager: JhiEventManager,
        private classUnitGroupService: ClassUnitGroupService,
        private route: ActivatedRoute
    ) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInClassUnitGroups();
    }

    load(id) {
        this.classUnitGroupService.find(id)
            .subscribe((classUnitGroupResponse: HttpResponse<ClassUnitGroup>) => {
                this.classUnitGroup = classUnitGroupResponse.body;
            });
    }
    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    registerChangeInClassUnitGroups() {
        this.eventSubscriber = this.eventManager.subscribe(
            'classUnitGroupListModification',
            (response) => this.load(this.classUnitGroup.id)
        );
    }
}
