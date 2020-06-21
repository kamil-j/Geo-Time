/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { Observable } from 'rxjs/Observable';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { GeoTimeTestModule } from '../../../test.module';
import { SubdepartmentComponent } from '../../../../../../main/webapp/app/entities/subdepartment/subdepartment.component';
import { SubdepartmentService } from '../../../../../../main/webapp/app/entities/subdepartment/subdepartment.service';
import { Subdepartment } from '../../../../../../main/webapp/app/shared/model/subdepartment.model';

describe('Component Tests', () => {

    describe('Subdepartment Management Component', () => {
        let comp: SubdepartmentComponent;
        let fixture: ComponentFixture<SubdepartmentComponent>;
        let service: SubdepartmentService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [GeoTimeTestModule],
                declarations: [SubdepartmentComponent],
                providers: [
                    SubdepartmentService
                ]
            })
            .overrideTemplate(SubdepartmentComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(SubdepartmentComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(SubdepartmentService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN
                const headers = new HttpHeaders().append('link', 'link;link');
                spyOn(service, 'query').and.returnValue(Observable.of(new HttpResponse({
                    body: [new Subdepartment(123)],
                    headers
                })));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(service.query).toHaveBeenCalled();
                expect(comp.subdepartments[0]).toEqual(jasmine.objectContaining({id: 123}));
            });
        });
    });

});
