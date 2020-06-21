/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { Observable } from 'rxjs/Observable';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { GeoTimeTestModule } from '../../../test.module';
import { SemesterComponent } from '../../../../../../main/webapp/app/entities/semester/semester.component';
import { SemesterService } from '../../../../../../main/webapp/app/entities/semester/semester.service';
import { Semester } from '../../../../../../main/webapp/app/shared/model/semester.model';

describe('Component Tests', () => {

    describe('Semester Management Component', () => {
        let comp: SemesterComponent;
        let fixture: ComponentFixture<SemesterComponent>;
        let service: SemesterService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [GeoTimeTestModule],
                declarations: [SemesterComponent],
                providers: [
                    SemesterService
                ]
            })
            .overrideTemplate(SemesterComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(SemesterComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(SemesterService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN
                const headers = new HttpHeaders().append('link', 'link;link');
                spyOn(service, 'query').and.returnValue(Observable.of(new HttpResponse({
                    body: [new Semester(123)],
                    headers
                })));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(service.query).toHaveBeenCalled();
                expect(comp.semesters[0]).toEqual(jasmine.objectContaining({id: 123}));
            });
        });
    });

});
