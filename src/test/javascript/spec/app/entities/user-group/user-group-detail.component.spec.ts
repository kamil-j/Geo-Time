/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { GeoTimeTestModule } from '../../../test.module';
import { UserGroupDetailComponent } from '../../../../../../main/webapp/app/entities/user-group/user-group-detail.component';
import { UserGroupService } from '../../../../../../main/webapp/app/entities/user-group/user-group.service';
import { UserGroup } from '../../../../../../main/webapp/app/shared/model/user-group.model';

describe('Component Tests', () => {

    describe('UserGroup Management Detail Component', () => {
        let comp: UserGroupDetailComponent;
        let fixture: ComponentFixture<UserGroupDetailComponent>;
        let service: UserGroupService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [GeoTimeTestModule],
                declarations: [UserGroupDetailComponent],
                providers: [
                    UserGroupService
                ]
            })
            .overrideTemplate(UserGroupDetailComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(UserGroupDetailComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(UserGroupService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                spyOn(service, 'find').and.returnValue(Observable.of(new HttpResponse({
                    body: new UserGroup(123)
                })));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(service.find).toHaveBeenCalledWith(123);
                expect(comp.userGroup).toEqual(jasmine.objectContaining({id: 123}));
            });
        });
    });

});
