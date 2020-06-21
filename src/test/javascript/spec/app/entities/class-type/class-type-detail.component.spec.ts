/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { GeoTimeTestModule } from '../../../test.module';
import { ClassTypeDetailComponent } from '../../../../../../main/webapp/app/entities/class-type/class-type-detail.component';
import { ClassTypeService } from '../../../../../../main/webapp/app/entities/class-type/class-type.service';
import { ClassType } from '../../../../../../main/webapp/app/shared/model/class-type.model';

describe('Component Tests', () => {

    describe('ClassType Management Detail Component', () => {
        let comp: ClassTypeDetailComponent;
        let fixture: ComponentFixture<ClassTypeDetailComponent>;
        let service: ClassTypeService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [GeoTimeTestModule],
                declarations: [ClassTypeDetailComponent],
                providers: [
                    ClassTypeService
                ]
            })
            .overrideTemplate(ClassTypeDetailComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(ClassTypeDetailComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(ClassTypeService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                spyOn(service, 'find').and.returnValue(Observable.of(new HttpResponse({
                    body: new ClassType(123)
                })));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(service.find).toHaveBeenCalledWith(123);
                expect(comp.classType).toEqual(jasmine.objectContaining({id: 123}));
            });
        });
    });

});
