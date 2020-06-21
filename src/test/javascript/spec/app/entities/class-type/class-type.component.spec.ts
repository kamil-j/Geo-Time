/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { Observable } from 'rxjs/Observable';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { GeoTimeTestModule } from '../../../test.module';
import { ClassTypeComponent } from '../../../../../../main/webapp/app/entities/class-type/class-type.component';
import { ClassTypeService } from '../../../../../../main/webapp/app/entities/class-type/class-type.service';
import { ClassType } from '../../../../../../main/webapp/app/shared/model/class-type.model';

describe('Component Tests', () => {

    describe('ClassType Management Component', () => {
        let comp: ClassTypeComponent;
        let fixture: ComponentFixture<ClassTypeComponent>;
        let service: ClassTypeService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [GeoTimeTestModule],
                declarations: [ClassTypeComponent],
                providers: [
                    ClassTypeService
                ]
            })
            .overrideTemplate(ClassTypeComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(ClassTypeComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(ClassTypeService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN
                const headers = new HttpHeaders().append('link', 'link;link');
                spyOn(service, 'query').and.returnValue(Observable.of(new HttpResponse({
                    body: [new ClassType(123)],
                    headers
                })));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(service.query).toHaveBeenCalled();
                expect(comp.classTypes[0]).toEqual(jasmine.objectContaining({id: 123}));
            });
        });
    });

});
