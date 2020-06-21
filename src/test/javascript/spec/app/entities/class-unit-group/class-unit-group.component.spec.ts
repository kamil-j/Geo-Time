/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { Observable } from 'rxjs/Observable';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { GeoTimeTestModule } from '../../../test.module';
import { ClassUnitGroupComponent } from '../../../../../../main/webapp/app/entities/class-unit-group/class-unit-group.component';
import { ClassUnitGroupService } from '../../../../../../main/webapp/app/entities/class-unit-group/class-unit-group.service';
import { ClassUnitGroup } from '../../../../../../main/webapp/app/shared/model/class-unit-group.model';

describe('Component Tests', () => {

    describe('ClassUnitGroup Management Component', () => {
        let comp: ClassUnitGroupComponent;
        let fixture: ComponentFixture<ClassUnitGroupComponent>;
        let service: ClassUnitGroupService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [GeoTimeTestModule],
                declarations: [ClassUnitGroupComponent],
                providers: [
                    ClassUnitGroupService
                ]
            })
            .overrideTemplate(ClassUnitGroupComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(ClassUnitGroupComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(ClassUnitGroupService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN
                const headers = new HttpHeaders().append('link', 'link;link');
                spyOn(service, 'query').and.returnValue(Observable.of(new HttpResponse({
                    body: [new ClassUnitGroup(123)],
                    headers
                })));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(service.query).toHaveBeenCalled();
                expect(comp.classUnitGroups[0]).toEqual(jasmine.objectContaining({id: 123}));
            });
        });
    });

});
