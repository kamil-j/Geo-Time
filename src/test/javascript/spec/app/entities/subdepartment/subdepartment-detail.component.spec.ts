/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { GeoTimeTestModule } from '../../../test.module';
import { SubdepartmentDetailComponent } from '../../../../../../main/webapp/app/entities/subdepartment/subdepartment-detail.component';
import { SubdepartmentService } from '../../../../../../main/webapp/app/entities/subdepartment/subdepartment.service';
import { Subdepartment } from '../../../../../../main/webapp/app/shared/model/subdepartment.model';

describe('Component Tests', () => {

    describe('Subdepartment Management Detail Component', () => {
        let comp: SubdepartmentDetailComponent;
        let fixture: ComponentFixture<SubdepartmentDetailComponent>;
        let service: SubdepartmentService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [GeoTimeTestModule],
                declarations: [SubdepartmentDetailComponent],
                providers: [
                    SubdepartmentService
                ]
            })
            .overrideTemplate(SubdepartmentDetailComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(SubdepartmentDetailComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(SubdepartmentService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                spyOn(service, 'find').and.returnValue(Observable.of(new HttpResponse({
                    body: new Subdepartment(123)
                })));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(service.find).toHaveBeenCalledWith(123);
                expect(comp.subdepartment).toEqual(jasmine.objectContaining({id: 123}));
            });
        });
    });

});
