/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { Observable } from 'rxjs/Observable';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { GeoTimeTestModule } from '../../../test.module';
import { ScheduleUnitComponent } from '../../../../../../main/webapp/app/entities/schedule-unit/schedule-unit.component';
import { ScheduleUnitService } from '../../../../../../main/webapp/app/entities/schedule-unit/schedule-unit.service';
import { ScheduleUnit } from '../../../../../../main/webapp/app/shared/model/schedule-unit.model';

describe('Component Tests', () => {

    describe('ScheduleUnit Management Component', () => {
        let comp: ScheduleUnitComponent;
        let fixture: ComponentFixture<ScheduleUnitComponent>;
        let service: ScheduleUnitService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [GeoTimeTestModule],
                declarations: [ScheduleUnitComponent],
                providers: [
                    ScheduleUnitService
                ]
            })
            .overrideTemplate(ScheduleUnitComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(ScheduleUnitComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(ScheduleUnitService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN
                const headers = new HttpHeaders().append('link', 'link;link');
                spyOn(service, 'query').and.returnValue(Observable.of(new HttpResponse({
                    body: [new ScheduleUnit(123)],
                    headers
                })));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(service.query).toHaveBeenCalled();
                expect(comp.scheduleUnits[0]).toEqual(jasmine.objectContaining({id: 123}));
            });
        });
    });

});
