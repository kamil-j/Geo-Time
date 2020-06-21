/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { Observable } from 'rxjs/Observable';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { GeoTimeTestModule } from '../../../test.module';
import { RoomTypeComponent } from '../../../../../../main/webapp/app/entities/room-type/room-type.component';
import { RoomTypeService } from '../../../../../../main/webapp/app/entities/room-type/room-type.service';
import { RoomType } from '../../../../../../main/webapp/app/shared/model/room-type.model';

describe('Component Tests', () => {

    describe('RoomType Management Component', () => {
        let comp: RoomTypeComponent;
        let fixture: ComponentFixture<RoomTypeComponent>;
        let service: RoomTypeService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [GeoTimeTestModule],
                declarations: [RoomTypeComponent],
                providers: [
                    RoomTypeService
                ]
            })
            .overrideTemplate(RoomTypeComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(RoomTypeComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(RoomTypeService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN
                const headers = new HttpHeaders().append('link', 'link;link');
                spyOn(service, 'query').and.returnValue(Observable.of(new HttpResponse({
                    body: [new RoomType(123)],
                    headers
                })));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(service.query).toHaveBeenCalled();
                expect(comp.roomTypes[0]).toEqual(jasmine.objectContaining({id: 123}));
            });
        });
    });

});
