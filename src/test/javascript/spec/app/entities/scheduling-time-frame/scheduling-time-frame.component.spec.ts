/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { Observable } from 'rxjs/Observable';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { GeoTimeTestModule } from '../../../test.module';
import { SchedulingTimeFrameComponent } from '../../../../../../main/webapp/app/entities/scheduling-time-frame/scheduling-time-frame.component';
import { SchedulingTimeFrameService } from '../../../../../../main/webapp/app/entities/scheduling-time-frame/scheduling-time-frame.service';
import { SchedulingTimeFrame } from '../../../../../../main/webapp/app/shared/model/scheduling-time-frame.model';

describe('Component Tests', () => {

    describe('SchedulingTimeFrame Management Component', () => {
        let comp: SchedulingTimeFrameComponent;
        let fixture: ComponentFixture<SchedulingTimeFrameComponent>;
        let service: SchedulingTimeFrameService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [GeoTimeTestModule],
                declarations: [SchedulingTimeFrameComponent],
                providers: [
                    SchedulingTimeFrameService
                ]
            })
            .overrideTemplate(SchedulingTimeFrameComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(SchedulingTimeFrameComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(SchedulingTimeFrameService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN
                const headers = new HttpHeaders().append('link', 'link;link');
                spyOn(service, 'query').and.returnValue(Observable.of(new HttpResponse({
                    body: [new SchedulingTimeFrame(123)],
                    headers
                })));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(service.query).toHaveBeenCalled();
                expect(comp.schedulingTimeFrames[0]).toEqual(jasmine.objectContaining({id: 123}));
            });
        });
    });

});
