import { Injectable, Component } from '@angular/core';
import { Router } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { HttpResponse } from '@angular/common/http';
import { AcademicUnit } from '../../shared';
import { AcademicUnitService } from './academic-unit.service';

@Injectable()
export class AcademicUnitPopupService {
    private ngbModalRef: NgbModalRef;

    constructor(
        private modalService: NgbModal,
        private router: Router,
        private academicUnitService: AcademicUnitService

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
                this.academicUnitService.find(id)
                    .subscribe((academicUnitResponse: HttpResponse<AcademicUnit>) => {
                        const academicUnit: AcademicUnit = academicUnitResponse.body;
                        this.ngbModalRef = this.academicUnitModalRef(component, academicUnit);
                        resolve(this.ngbModalRef);
                    });
            } else {
                // setTimeout used as a workaround for getting ExpressionChangedAfterItHasBeenCheckedError
                setTimeout(() => {
                    this.ngbModalRef = this.academicUnitModalRef(component, new AcademicUnit());
                    resolve(this.ngbModalRef);
                }, 0);
            }
        });
    }

    academicUnitModalRef(component: Component, academicUnit: AcademicUnit): NgbModalRef {
        const modalRef = this.modalService.open(component, { size: 'lg', backdrop: 'static'});
        modalRef.componentInstance.academicUnit = academicUnit;
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
