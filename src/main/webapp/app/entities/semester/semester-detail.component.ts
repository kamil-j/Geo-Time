import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs/Subscription';
import { JhiEventManager } from 'ng-jhipster';
import { Semester } from '../../shared';
import { SemesterService } from './semester.service';

@Component({
    selector: 'jhi-semester-detail',
    templateUrl: './semester-detail.component.html'
})
export class SemesterDetailComponent implements OnInit, OnDestroy {

    semester: Semester;
    private subscription: Subscription;
    private eventSubscriber: Subscription;

    constructor(
        private eventManager: JhiEventManager,
        private semesterService: SemesterService,
        private route: ActivatedRoute
    ) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInSemesters();
    }

    load(id) {
        this.semesterService.find(id)
            .subscribe((semesterResponse: HttpResponse<Semester>) => {
                this.semester = semesterResponse.body;
            });
    }
    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    registerChangeInSemesters() {
        this.eventSubscriber = this.eventManager.subscribe(
            'semesterListModification',
            (response) => this.load(this.semester.id)
        );
    }
}
