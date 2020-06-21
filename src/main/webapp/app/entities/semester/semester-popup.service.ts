import { Injectable, Component } from '@angular/core';
import { Router } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { HttpResponse } from '@angular/common/http';
import { DatePipe } from '@angular/common';
import { Semester } from '../../shared';
import { SemesterService } from './semester.service';

@Injectable()
export class SemesterPopupService {
    private ngbModalRef: NgbModalRef;

    constructor(
        private datePipe: DatePipe,
        private modalService: NgbModal,
        private router: Router,
        private semesterService: SemesterService

    ) {
        this.ngbModalRef = null;
    }

    open(component: Component, id?: number | any): Promise<NgbModalRef> {
        return new Promise<NgbModalRef>((resolve, reject) => {
            const isOpen = this.ngbModalRef !== null;
            if (isOpen) {
                resolve(this.ngbModalRef);
            }

            if (id) {
                this.semesterService.find(id)
                    .subscribe((semesterResponse: HttpResponse<Semester>) => {
                        const semester: Semester = semesterResponse.body;
                        semester.startDate = this.datePipe
                            .transform(semester.startDate, 'yyyy-MM-ddTHH:mm:ss');
                        semester.endDate = this.datePipe
                            .transform(semester.endDate, 'yyyy-MM-ddTHH:mm:ss');
                        this.ngbModalRef = this.semesterModalRef(component, semester);
                        resolve(this.ngbModalRef);
                    });
            } else {
                // setTimeout used as a workaround for getting ExpressionChangedAfterItHasBeenCheckedError
                setTimeout(() => {
                    this.ngbModalRef = this.semesterModalRef(component, new Semester());
                    resolve(this.ngbModalRef);
                }, 0);
            }
        });
    }

    semesterModalRef(component: Component, semester: Semester): NgbModalRef {
        const modalRef = this.modalService.open(component, { size: 'lg', backdrop: 'static'});
        modalRef.componentInstance.semester = semester;
        modalRef.result.then((result) => {
            this.router.navigate([{ outlets: { popup: null }}], { replaceUrl: true, queryParamsHandling: 'merge' });
            this.ngbModalRef = null;
        }, (reason) => {
            this.router.navigate([{ outlets: { popup: null }}], { replaceUrl: true, queryParamsHandling: 'merge' });
            this.ngbModalRef = null;
        });
        return modalRef;
    }
}
