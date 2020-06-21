/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { Observable } from 'rxjs/Observable';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { GeoTimeTestModule } from '../../../test.module';
import { BookingUnitComponent } from '../../../../../../main/webapp/app/entities/booking-unit/booking-unit.component';
import { BookingUnitService } from '../../../../../../main/webapp/app/entities/booking-unit/booking-unit.service';
import { BookingUnit } from '../../../../../../main/webapp/app/shared/model/booking-unit.model';

describe('Component Tests', () => {

    describe('BookingUnit Management Component', () => {
        let comp: BookingUnitComponent;
        let fixture: ComponentFixture<BookingUnitComponent>;
        let service: BookingUnitService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [GeoTimeTestModule],
                declarations: [BookingUnitComponent],
                providers: [
                    BookingUnitService
                ]
            })
            .overrideTemplate(BookingUnitComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(BookingUnitComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(BookingUnitService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN
                const headers = new HttpHeaders().append('link', 'link;link');
                spyOn(service, 'query').and.returnValue(Observable.of(new HttpResponse({
                    body: [new BookingUnit(123)],
                    headers
                })));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(service.query).toHaveBeenCalled();
                expect(comp.bookingUnits[0]).toEqual(jasmine.objectContaining({id: 123}));
            });
        });
    });

});
