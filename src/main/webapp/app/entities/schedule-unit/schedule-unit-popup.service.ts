import { Injectable, Component } from '@angular/core';
import { Router } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { HttpResponse } from '@angular/common/http';
import { DatePipe } from '@angular/common';
import { ScheduleUnit } from '../../shared';
import { ScheduleUnitService } from './schedule-unit.service';

@Injectable()
export class ScheduleUnitPopupService {
    private ngbModalRef: NgbModalRef;

    constructor(
        private datePipe: DatePipe,
        private modalService: NgbModal,
        private router: Router,
        private scheduleUnitService: ScheduleUnitService

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
                this.scheduleUnitService.find(id)
                    .subscribe((scheduleUnitResponse: HttpResponse<ScheduleUnit>) => {
                        const scheduleUnit: ScheduleUnit = scheduleUnitResponse.body;
                        scheduleUnit.startDate = this.datePipe
                            .transform(scheduleUnit.startDate, 'yyyy-MM-ddTHH:mm:ss');
                        scheduleUnit.endDate = this.datePipe
                            .transform(scheduleUnit.endDate, 'yyyy-MM-ddTHH:mm:ss');
                        this.ngbModalRef = this.scheduleUnitModalRef(component, scheduleUnit);
                        resolve(this.ngbModalRef);
                    });
            } else {
                // setTimeout used as a workaround for getting ExpressionChangedAfterItHasBeenCheckedError
                setTimeout(() => {
                    this.ngbModalRef = this.scheduleUnitModalRef(component, new ScheduleUnit());
                    resolve(this.ngbModalRef);
                }, 0);
            }
        });
    }

    scheduleUnitModalRef(component: Component, scheduleUnit: ScheduleUnit): NgbModalRef {
        const modalRef = this.modalService.open(component, { size: 'lg', backdrop: 'static'});
        modalRef.componentInstance.scheduleUnit = scheduleUnit;
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
