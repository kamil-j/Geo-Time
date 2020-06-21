/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { Observable } from 'rxjs/Observable';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { GeoTimeTestModule } from '../../../test.module';
import { StudyFieldComponent } from '../../../../../../main/webapp/app/entities/study-field/study-field.component';
import { StudyFieldService } from '../../../../../../main/webapp/app/entities/study-field/study-field.service';
import { StudyField } from '../../../../../../main/webapp/app/shared/model/study-field.model';

describe('Component Tests', () => {

    describe('StudyField Management Component', () => {
        let comp: StudyFieldComponent;
        let fixture: ComponentFixture<StudyFieldComponent>;
        let service: StudyFieldService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [GeoTimeTestModule],
                declarations: [StudyFieldComponent],
                providers: [
                    StudyFieldService
                ]
            })
            .overrideTemplate(StudyFieldComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(StudyFieldComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(StudyFieldService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN
                const headers = new HttpHeaders().append('link', 'link;link');
                spyOn(service, 'query').and.returnValue(Observable.of(new HttpResponse({
                    body: [new StudyField(123)],
                    headers
                })));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(service.query).toHaveBeenCalled();
                expect(comp.studyFields[0]).toEqual(jasmine.objectContaining({id: 123}));
            });
        });
    });

});
