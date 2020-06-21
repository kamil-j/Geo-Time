/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { Observable } from 'rxjs/Observable';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { GeoTimeTestModule } from '../../../test.module';
import { UserGroupComponent } from '../../../../../../main/webapp/app/entities/user-group/user-group.component';
import { UserGroupService } from '../../../../../../main/webapp/app/entities/user-group/user-group.service';
import { UserGroup } from '../../../../../../main/webapp/app/shared/model/user-group.model';

describe('Component Tests', () => {

    describe('UserGroup Management Component', () => {
        let comp: UserGroupComponent;
        let fixture: ComponentFixture<UserGroupComponent>;
        let service: UserGroupService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [GeoTimeTestModule],
                declarations: [UserGroupComponent],
                providers: [
                    UserGroupService
                ]
            })
            .overrideTemplate(UserGroupComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(UserGroupComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(UserGroupService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN
                const headers = new HttpHeaders().append('link', 'link;link');
                spyOn(service, 'query').and.returnValue(Observable.of(new HttpResponse({
                    body: [new UserGroup(123)],
                    headers
                })));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(service.query).toHaveBeenCalled();
                expect(comp.userGroups[0]).toEqual(jasmine.objectContaining({id: 123}));
            });
        });
    });

});
