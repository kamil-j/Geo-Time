/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { GeoTimeTestModule } from '../../../test.module';
import { StudyFieldDetailComponent } from '../../../../../../main/webapp/app/entities/study-field/study-field-detail.component';
import { StudyFieldService } from '../../../../../../main/webapp/app/entities/study-field/study-field.service';
import { StudyField } from '../../../../../../main/webapp/app/shared/model/study-field.model';

describe('Component Tests', () => {

    describe('StudyField Management Detail Component', () => {
        let comp: StudyFieldDetailComponent;
        let fixture: ComponentFixture<StudyFieldDetailComponent>;
        let service: StudyFieldService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [GeoTimeTestModule],
                declarations: [StudyFieldDetailComponent],
                providers: [
                    StudyFieldService
                ]
            })
            .overrideTemplate(StudyFieldDetailComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(StudyFieldDetailComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(StudyFieldService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                spyOn(service, 'find').and.returnValue(Observable.of(new HttpResponse({
                    body: new StudyField(123)
                })));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(service.find).toHaveBeenCalledWith(123);
                expect(comp.studyField).toEqual(jasmine.objectContaining({id: 123}));
            });
        });
    });

});
