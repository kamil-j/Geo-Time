import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';
import { Semester } from '../../shared';
import { SemesterPopupService } from './semester-popup.service';
import { SemesterService } from './semester.service';

@Component({
    selector: 'jhi-semester-delete-dialog',
    templateUrl: './semester-delete-dialog.component.html'
})
export class SemesterDeleteDialogComponent {

    semester: Semester;

    constructor(
        private semesterService: SemesterService,
        public activeModal: NgbActiveModal,
        private eventManager: JhiEventManager
    ) {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.semesterService.delete(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: 'semesterListModification',
                content: 'Deleted an semester'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-semester-delete-popup',
    template: ''
})
export class SemesterDeletePopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private semesterPopupService: SemesterPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.semesterPopupService
                .open(SemesterDeleteDialogComponent as Component, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
