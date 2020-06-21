/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async, inject, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable } from 'rxjs/Observable';
import { JhiEventManager } from 'ng-jhipster';

import { GeoTimeTestModule } from '../../../test.module';
import { BookingUnitDialogComponent } from '../../../../../../main/webapp/app/entities/booking-unit/booking-unit-dialog.component';
import { BookingUnitService } from '../../../../../../main/webapp/app/entities/booking-unit/booking-unit.service';
import { BookingUnit } from '../../../../../../main/webapp/app/shared/model/booking-unit.model';
import { ClassUnitService } from '../../../../../../main/webapp/app/entities/class-unit';
import { RoomService } from '../../../../../../main/webapp/app/entities/room';

describe('Component Tests', () => {

    describe('BookingUnit Management Dialog Component', () => {
        let comp: BookingUnitDialogComponent;
        let fixture: ComponentFixture<BookingUnitDialogComponent>;
        let service: BookingUnitService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [GeoTimeTestModule],
                declarations: [BookingUnitDialogComponent],
                providers: [
                    ClassUnitService,
                    RoomService,
                    BookingUnitService
                ]
            })
            .overrideTemplate(BookingUnitDialogComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(BookingUnitDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(BookingUnitService);
            mockEventManager = fixture.debugElement.injector.get(JhiEventManager);
            mockActiveModal = fixture.debugElement.injector.get(NgbActiveModal);
        });

        describe('save', () => {
            it('Should call update service on save for existing entity',
                inject([],
                    fakeAsync(() => {
                        // GIVEN
                        const entity = new BookingUnit(123);
                        spyOn(service, 'update').and.returnValue(Observable.of(new HttpResponse({body: entity})));
                        comp.bookingUnit = entity;
                        // WHEN
                        comp.save();
                        tick(); // simulate async

                        // THEN
                        expect(service.update).toHaveBeenCalledWith(entity);
                        expect(comp.isSaving).toEqual(false);
                        expect(mockEventManager.broadcastSpy).toHaveBeenCalledWith({ name: 'bookingUnitListModification', content: 'OK'});
                        expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
                    })
                )
            );

            it('Should call create service on save for new entity',
                inject([],
                    fakeAsync(() => {
                        // GIVEN
                        const entity = new BookingUnit();
                        spyOn(service, 'create').and.returnValue(Observable.of(new HttpResponse({body: entity})));
                        comp.bookingUnit = entity;
                        // WHEN
                        comp.save();
                        tick(); // simulate async

                        // THEN
                        expect(service.create).toHaveBeenCalledWith(entity);
                        expect(comp.isSaving).toEqual(false);
                        expect(mockEventManager.broadcastSpy).toHaveBeenCalledWith({ name: 'bookingUnitListModification', content: 'OK'});
                        expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
                    })
                )
            );
        });
    });

});
