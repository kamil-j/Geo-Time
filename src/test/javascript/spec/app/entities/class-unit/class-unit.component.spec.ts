/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { Observable } from 'rxjs/Observable';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { GeoTimeTestModule } from '../../../test.module';
import { ClassUnitComponent } from '../../../../../../main/webapp/app/entities/class-unit/class-unit.component';
import { ClassUnitService } from '../../../../../../main/webapp/app/entities/class-unit/class-unit.service';
import { ClassUnit } from '../../../../../../main/webapp/app/shared/model/class-unit.model';

describe('Component Tests', () => {

    describe('ClassUnit Management Component', () => {
        let comp: ClassUnitComponent;
        let fixture: ComponentFixture<ClassUnitComponent>;
        let service: ClassUnitService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [GeoTimeTestModule],
                declarations: [ClassUnitComponent],
                providers: [
                    ClassUnitService
                ]
            })
            .overrideTemplate(ClassUnitComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(ClassUnitComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(ClassUnitService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN
                const headers = new HttpHeaders().append('link', 'link;link');
                spyOn(service, 'query').and.returnValue(Observable.of(new HttpResponse({
                    body: [new ClassUnit(123)],
                    headers
                })));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(service.query).toHaveBeenCalled();
                expect(comp.classUnits[0]).toEqual(jasmine.objectContaining({id: 123}));
            });
        });
    });

});
