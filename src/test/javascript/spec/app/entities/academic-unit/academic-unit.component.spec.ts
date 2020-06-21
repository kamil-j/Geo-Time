/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { Observable } from 'rxjs/Observable';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { GeoTimeTestModule } from '../../../test.module';
import { AcademicUnitComponent } from '../../../../../../main/webapp/app/entities/academic-unit/academic-unit.component';
import { AcademicUnitService } from '../../../../../../main/webapp/app/entities/academic-unit/academic-unit.service';
import { AcademicUnit } from '../../../../../../main/webapp/app/shared/model/academic-unit.model';

describe('Component Tests', () => {

    describe('AcademicUnit Management Component', () => {
        let comp: AcademicUnitComponent;
        let fixture: ComponentFixture<AcademicUnitComponent>;
        let service: AcademicUnitService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [GeoTimeTestModule],
                declarations: [AcademicUnitComponent],
                providers: [
                    AcademicUnitService
                ]
            })
            .overrideTemplate(AcademicUnitComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(AcademicUnitComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(AcademicUnitService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN
                const headers = new HttpHeaders().append('link', 'link;link');
                spyOn(service, 'query').and.returnValue(Observable.of(new HttpResponse({
                    body: [new AcademicUnit(123)],
                    headers
                })));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(service.query).toHaveBeenCalled();
                expect(comp.academicUnits[0]).toEqual(jasmine.objectContaining({id: 123}));
            });
        });
    });

});
