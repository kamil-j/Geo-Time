import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';
import { StudyField } from '../../shared';
import { StudyFieldPopupService } from './study-field-popup.service';
import { StudyFieldService } from './study-field.service';

@Component({
    selector: 'jhi-study-field-delete-dialog',
    templateUrl: './study-field-delete-dialog.component.html'
})
export class StudyFieldDeleteDialogComponent {

    studyField: StudyField;

    constructor(
        private studyFieldService: StudyFieldService,
        public activeModal: NgbActiveModal,
        private eventManager: JhiEventManager
    ) {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.studyFieldService.delete(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: 'studyFieldListModification',
                content: 'Deleted an studyField'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-study-field-delete-popup',
    template: ''
})
export class StudyFieldDeletePopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private studyFieldPopupService: StudyFieldPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.studyFieldPopupService
                .open(StudyFieldDeleteDialogComponent as Component, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
