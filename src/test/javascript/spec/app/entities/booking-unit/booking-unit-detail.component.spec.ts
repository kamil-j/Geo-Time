/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { GeoTimeTestModule } from '../../../test.module';
import { BookingUnitDetailComponent } from '../../../../../../main/webapp/app/entities/booking-unit/booking-unit-detail.component';
import { BookingUnitService } from '../../../../../../main/webapp/app/entities/booking-unit/booking-unit.service';
import { BookingUnit } from '../../../../../../main/webapp/app/shared/model/booking-unit.model';

describe('Component Tests', () => {

    describe('BookingUnit Management Detail Component', () => {
        let comp: BookingUnitDetailComponent;
        let fixture: ComponentFixture<BookingUnitDetailComponent>;
        let service: BookingUnitService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [GeoTimeTestModule],
                declarations: [BookingUnitDetailComponent],
                providers: [
                    BookingUnitService
                ]
            })
            .overrideTemplate(BookingUnitDetailComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(BookingUnitDetailComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(BookingUnitService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                spyOn(service, 'find').and.returnValue(Observable.of(new HttpResponse({
                    body: new BookingUnit(123)
                })));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(service.find).toHaveBeenCalledWith(123);
                expect(comp.bookingUnit).toEqual(jasmine.objectContaining({id: 123}));
            });
        });
    });

});
