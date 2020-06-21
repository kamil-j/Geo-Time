/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { GeoTimeTestModule } from '../../../test.module';
import { ScheduleUnitDetailComponent } from '../../../../../../main/webapp/app/entities/schedule-unit/schedule-unit-detail.component';
import { ScheduleUnitService } from '../../../../../../main/webapp/app/entities/schedule-unit/schedule-unit.service';
import { ScheduleUnit } from '../../../../../../main/webapp/app/shared/model/schedule-unit.model';

describe('Component Tests', () => {

    describe('ScheduleUnit Management Detail Component', () => {
        let comp: ScheduleUnitDetailComponent;
        let fixture: ComponentFixture<ScheduleUnitDetailComponent>;
        let service: ScheduleUnitService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [GeoTimeTestModule],
                declarations: [ScheduleUnitDetailComponent],
                providers: [
                    ScheduleUnitService
                ]
            })
            .overrideTemplate(ScheduleUnitDetailComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(ScheduleUnitDetailComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(ScheduleUnitService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                spyOn(service, 'find').and.returnValue(Observable.of(new HttpResponse({
                    body: new ScheduleUnit(123)
                })));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(service.find).toHaveBeenCalledWith(123);
                expect(comp.scheduleUnit).toEqual(jasmine.objectContaining({id: 123}));
            });
        });
    });

});
