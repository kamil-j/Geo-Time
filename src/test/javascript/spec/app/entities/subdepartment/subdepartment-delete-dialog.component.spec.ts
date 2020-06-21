/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable } from 'rxjs/Observable';
import { JhiEventManager } from 'ng-jhipster';

import { GeoTimeTestModule } from '../../../test.module';
import { SubdepartmentDeleteDialogComponent } from '../../../../../../main/webapp/app/entities/subdepartment/subdepartment-delete-dialog.component';
import { SubdepartmentService } from '../../../../../../main/webapp/app/entities/subdepartment/subdepartment.service';

describe('Component Tests', () => {

    describe('Subdepartment Management Delete Component', () => {
        let comp: SubdepartmentDeleteDialogComponent;
        let fixture: ComponentFixture<SubdepartmentDeleteDialogComponent>;
        let service: SubdepartmentService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [GeoTimeTestModule],
                declarations: [SubdepartmentDeleteDialogComponent],
                providers: [
                    SubdepartmentService
                ]
            })
            .overrideTemplate(SubdepartmentDeleteDialogComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(SubdepartmentDeleteDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(SubdepartmentService);
            mockEventManager = fixture.debugElement.injector.get(JhiEventManager);
            mockActiveModal = fixture.debugElement.injector.get(NgbActiveModal);
        });

        describe('confirmDelete', () => {
            it('Should call delete service on confirmDelete',
                inject([],
                    fakeAsync(() => {
                        // GIVEN
                        spyOn(service, 'delete').and.returnValue(Observable.of({}));

                        // WHEN
                        comp.confirmDelete(123);
                        tick();

                        // THEN
                        expect(service.delete).toHaveBeenCalledWith(123);
                        expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
                        expect(mockEventManager.broadcastSpy).toHaveBeenCalled();
                    })
                )
            );
        });
    });

});
