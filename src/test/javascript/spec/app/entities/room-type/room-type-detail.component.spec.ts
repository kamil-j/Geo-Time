/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { GeoTimeTestModule } from '../../../test.module';
import { RoomTypeDetailComponent } from '../../../../../../main/webapp/app/entities/room-type/room-type-detail.component';
import { RoomTypeService } from '../../../../../../main/webapp/app/entities/room-type/room-type.service';
import { RoomType } from '../../../../../../main/webapp/app/shared/model/room-type.model';

describe('Component Tests', () => {

    describe('RoomType Management Detail Component', () => {
        let comp: RoomTypeDetailComponent;
        let fixture: ComponentFixture<RoomTypeDetailComponent>;
        let service: RoomTypeService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [GeoTimeTestModule],
                declarations: [RoomTypeDetailComponent],
                providers: [
                    RoomTypeService
                ]
            })
            .overrideTemplate(RoomTypeDetailComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(RoomTypeDetailComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(RoomTypeService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                spyOn(service, 'find').and.returnValue(Observable.of(new HttpResponse({
                    body: new RoomType(123)
                })));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(service.find).toHaveBeenCalledWith(123);
                expect(comp.roomType).toEqual(jasmine.objectContaining({id: 123}));
            });
        });
    });

});
