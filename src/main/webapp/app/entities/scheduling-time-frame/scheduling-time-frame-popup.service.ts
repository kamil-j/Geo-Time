import { Injectable, Component } from '@angular/core';
import { Router } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { HttpResponse } from '@angular/common/http';
import { DatePipe } from '@angular/common';
import { SchedulingTimeFrame } from '../../shared';
import { SchedulingTimeFrameService } from './scheduling-time-frame.service';

@Injectable()
export class SchedulingTimeFramePopupService {
    private ngbModalRef: NgbModalRef;

    constructor(
        private datePipe: DatePipe,
        private modalService: NgbModal,
        private router: Router,
        private schedulingTimeFrameService: SchedulingTimeFrameService

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
                this.schedulingTimeFrameService.find(id)
                    .subscribe((schedulingTimeFrameResponse: HttpResponse<SchedulingTimeFrame>) => {
                        const schedulingTimeFrame: SchedulingTimeFrame = schedulingTimeFrameResponse.body;
                        schedulingTimeFrame.startTime = this.datePipe
                            .transform(schedulingTimeFrame.startTime, 'yyyy-MM-ddTHH:mm:ss');
                        schedulingTimeFrame.endTime = this.datePipe
                            .transform(schedulingTimeFrame.endTime, 'yyyy-MM-ddTHH:mm:ss');
                        this.ngbModalRef = this.schedulingTimeFrameModalRef(component, schedulingTimeFrame);
                        resolve(this.ngbModalRef);
                    });
            } else {
                // setTimeout used as a workaround for getting ExpressionChangedAfterItHasBeenCheckedError
                setTimeout(() => {
                    this.ngbModalRef = this.schedulingTimeFrameModalRef(component, new SchedulingTimeFrame());
                    resolve(this.ngbModalRef);
                }, 0);
            }
        });
    }

    schedulingTimeFrameModalRef(component: Component, schedulingTimeFrame: SchedulingTimeFrame): NgbModalRef {
        const modalRef = this.modalService.open(component, { size: 'lg', backdrop: 'static'});
        modalRef.componentInstance.schedulingTimeFrame = schedulingTimeFrame;
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
