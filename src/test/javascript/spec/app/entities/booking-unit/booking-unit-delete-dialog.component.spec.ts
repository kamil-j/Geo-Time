/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable } from 'rxjs/Observable';
import { JhiEventManager } from 'ng-jhipster';

import { GeoTimeTestModule } from '../../../test.module';
import { BookingUnitDeleteDialogComponent } from '../../../../../../main/webapp/app/entities/booking-unit/booking-unit-delete-dialog.component';
import { BookingUnitService } from '../../../../../../main/webapp/app/entities/booking-unit/booking-unit.service';

describe('Component Tests', () => {

    describe('BookingUnit Management Delete Component', () => {
        let comp: BookingUnitDeleteDialogComponent;
        let fixture: ComponentFixture<BookingUnitDeleteDialogComponent>;
        let service: BookingUnitService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [GeoTimeTestModule],
                declarations: [BookingUnitDeleteDialogComponent],
                providers: [
                    BookingUnitService
                ]
            })
            .overrideTemplate(BookingUnitDeleteDialogComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(BookingUnitDeleteDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(BookingUnitService);
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
