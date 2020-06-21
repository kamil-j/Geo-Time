import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs/Subscription';
import { JhiEventManager } from 'ng-jhipster';
import { ClassType } from '../../shared';
import { ClassTypeService } from './class-type.service';

@Component({
    selector: 'jhi-class-type-detail',
    templateUrl: './class-type-detail.component.html'
})
export class ClassTypeDetailComponent implements OnInit, OnDestroy {

    classType: ClassType;
    private subscription: Subscription;
    private eventSubscriber: Subscription;

    constructor(
        private eventManager: JhiEventManager,
        private classTypeService: ClassTypeService,
        private route: ActivatedRoute
    ) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInClassTypes();
    }

    load(id) {
        this.classTypeService.find(id)
            .subscribe((classTypeResponse: HttpResponse<ClassType>) => {
                this.classType = classTypeResponse.body;
            });
    }
    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    registerChangeInClassTypes() {
        this.eventSubscriber = this.eventManager.subscribe(
            'classTypeListModification',
            (response) => this.load(this.classType.id)
        );
    }
}
