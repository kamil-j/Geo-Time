/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { GeoTimeTestModule } from '../../../test.module';
import { RoomDetailComponent } from '../../../../../../main/webapp/app/entities/room/room-detail.component';
import { RoomService } from '../../../../../../main/webapp/app/entities/room/room.service';
import { Room } from '../../../../../../main/webapp/app/shared/model/room.model';

describe('Component Tests', () => {

    describe('Room Management Detail Component', () => {
        let comp: RoomDetailComponent;
        let fixture: ComponentFixture<RoomDetailComponent>;
        let service: RoomService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [GeoTimeTestModule],
                declarations: [RoomDetailComponent],
                providers: [
                    RoomService
                ]
            })
            .overrideTemplate(RoomDetailComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(RoomDetailComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(RoomService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                spyOn(service, 'find').and.returnValue(Observable.of(new HttpResponse({
                    body: new Room(123)
                })));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(service.find).toHaveBeenCalledWith(123);
                expect(comp.room).toEqual(jasmine.objectContaining({id: 123}));
            });
        });
    });

});
