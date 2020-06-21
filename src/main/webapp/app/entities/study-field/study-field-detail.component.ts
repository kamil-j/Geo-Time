import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs/Subscription';
import { JhiEventManager } from 'ng-jhipster';
import { StudyField } from '../../shared';
import { StudyFieldService } from './study-field.service';

@Component({
    selector: 'jhi-study-field-detail',
    templateUrl: './study-field-detail.component.html'
})
export class StudyFieldDetailComponent implements OnInit, OnDestroy {

    studyField: StudyField;
    private subscription: Subscription;
    private eventSubscriber: Subscription;

    constructor(
        private eventManager: JhiEventManager,
        private studyFieldService: StudyFieldService,
        private route: ActivatedRoute
    ) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInStudyFields();
    }

    load(id) {
        this.studyFieldService.find(id)
            .subscribe((studyFieldResponse: HttpResponse<StudyField>) => {
                this.studyField = studyFieldResponse.body;
            });
    }
    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    registerChangeInStudyFields() {
        this.eventSubscriber = this.eventManager.subscribe(
            'studyFieldListModification',
            (response) => this.load(this.studyField.id)
        );
    }
}
