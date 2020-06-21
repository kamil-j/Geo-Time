/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { GeoTimeTestModule } from '../../../test.module';
import { ClassUnitDetailComponent } from '../../../../../../main/webapp/app/entities/class-unit/class-unit-detail.component';
import { ClassUnitService } from '../../../../../../main/webapp/app/entities/class-unit/class-unit.service';
import { ClassUnit } from '../../../../../../main/webapp/app/shared/model/class-unit.model';

describe('Component Tests', () => {

    describe('ClassUnit Management Detail Component', () => {
        let comp: ClassUnitDetailComponent;
        let fixture: ComponentFixture<ClassUnitDetailComponent>;
        let service: ClassUnitService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [GeoTimeTestModule],
                declarations: [ClassUnitDetailComponent],
                providers: [
                    ClassUnitService
                ]
            })
            .overrideTemplate(ClassUnitDetailComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(ClassUnitDetailComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(ClassUnitService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                spyOn(service, 'find').and.returnValue(Observable.of(new HttpResponse({
                    body: new ClassUnit(123)
                })));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(service.find).toHaveBeenCalledWith(123);
                expect(comp.classUnit).toEqual(jasmine.objectContaining({id: 123}));
            });
        });
    });

});
