/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { GeoTimeTestModule } from '../../../test.module';
import { AcademicUnitDetailComponent } from '../../../../../../main/webapp/app/entities/academic-unit/academic-unit-detail.component';
import { AcademicUnitService } from '../../../../../../main/webapp/app/entities/academic-unit/academic-unit.service';
import { AcademicUnit } from '../../../../../../main/webapp/app/shared/model/academic-unit.model';

describe('Component Tests', () => {

    describe('AcademicUnit Management Detail Component', () => {
        let comp: AcademicUnitDetailComponent;
        let fixture: ComponentFixture<AcademicUnitDetailComponent>;
        let service: AcademicUnitService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [GeoTimeTestModule],
                declarations: [AcademicUnitDetailComponent],
                providers: [
                    AcademicUnitService
                ]
            })
            .overrideTemplate(AcademicUnitDetailComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(AcademicUnitDetailComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(AcademicUnitService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                spyOn(service, 'find').and.returnValue(Observable.of(new HttpResponse({
                    body: new AcademicUnit(123)
                })));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(service.find).toHaveBeenCalledWith(123);
                expect(comp.academicUnit).toEqual(jasmine.objectContaining({id: 123}));
            });
        });
    });

});
