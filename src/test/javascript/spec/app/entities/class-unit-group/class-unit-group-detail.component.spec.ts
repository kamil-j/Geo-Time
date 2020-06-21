/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { GeoTimeTestModule } from '../../../test.module';
import { ClassUnitGroupDetailComponent } from '../../../../../../main/webapp/app/entities/class-unit-group/class-unit-group-detail.component';
import { ClassUnitGroupService } from '../../../../../../main/webapp/app/entities/class-unit-group/class-unit-group.service';
import { ClassUnitGroup } from '../../../../../../main/webapp/app/shared/model/class-unit-group.model';

describe('Component Tests', () => {

    describe('ClassUnitGroup Management Detail Component', () => {
        let comp: ClassUnitGroupDetailComponent;
        let fixture: ComponentFixture<ClassUnitGroupDetailComponent>;
        let service: ClassUnitGroupService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [GeoTimeTestModule],
                declarations: [ClassUnitGroupDetailComponent],
                providers: [
                    ClassUnitGroupService
                ]
            })
            .overrideTemplate(ClassUnitGroupDetailComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(ClassUnitGroupDetailComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(ClassUnitGroupService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                spyOn(service, 'find').and.returnValue(Observable.of(new HttpResponse({
                    body: new ClassUnitGroup(123)
                })));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(service.find).toHaveBeenCalledWith(123);
                expect(comp.classUnitGroup).toEqual(jasmine.objectContaining({id: 123}));
            });
        });
    });

});
