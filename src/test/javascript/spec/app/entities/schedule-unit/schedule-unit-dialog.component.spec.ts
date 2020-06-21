/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async, inject, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable } from 'rxjs/Observable';
import { JhiEventManager } from 'ng-jhipster';

import { GeoTimeTestModule } from '../../../test.module';
import { ScheduleUnitDialogComponent } from '../../../../../../main/webapp/app/entities/schedule-unit/schedule-unit-dialog.component';
import { ScheduleUnitService } from '../../../../../../main/webapp/app/entities/schedule-unit/schedule-unit.service';
import { ScheduleUnit } from '../../../../../../main/webapp/app/shared/model/schedule-unit.model';
import { ClassUnitService } from '../../../../../../main/webapp/app/entities/class-unit';
import { RoomService } from '../../../../../../main/webapp/app/entities/room';

describe('Component Tests', () => {

    describe('ScheduleUnit Management Dialog Component', () => {
        let comp: ScheduleUnitDialogComponent;
        let fixture: ComponentFixture<ScheduleUnitDialogComponent>;
        let service: ScheduleUnitService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [GeoTimeTestModule],
                declarations: [ScheduleUnitDialogComponent],
                providers: [
                    ClassUnitService,
                    RoomService,
                    ScheduleUnitService
                ]
            })
            .overrideTemplate(ScheduleUnitDialogComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(ScheduleUnitDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(ScheduleUnitService);
            mockEventManager = fixture.debugElement.injector.get(JhiEventManager);
            mockActiveModal = fixture.debugElement.injector.get(NgbActiveModal);
        });

        describe('save', () => {
            it('Should call update service on save for existing entity',
                inject([],
                    fakeAsync(() => {
                        // GIVEN
                        const entity = new ScheduleUnit(123);
                        spyOn(service, 'update').and.returnValue(Observable.of(new HttpResponse({body: entity})));
                        comp.scheduleUnit = entity;
                        // WHEN
                        comp.save();
                        tick(); // simulate async

                        // THEN
                        expect(service.update).toHaveBeenCalledWith(entity);
                        expect(comp.isSaving).toEqual(false);
                        expect(mockEventManager.broadcastSpy).toHaveBeenCalledWith({ name: 'scheduleUnitListModification', content: 'OK'});
                        expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
                    })
                )
            );

            it('Should call create service on save for new entity',
                inject([],
                    fakeAsync(() => {
                        // GIVEN
                        const entity = new ScheduleUnit();
                        spyOn(service, 'create').and.returnValue(Observable.of(new HttpResponse({body: entity})));
                        comp.scheduleUnit = entity;
                        // WHEN
                        comp.save();
                        tick(); // simulate async

                        // THEN
                        expect(service.create).toHaveBeenCalledWith(entity);
                        expect(comp.isSaving).toEqual(false);
                        expect(mockEventManager.broadcastSpy).toHaveBeenCalledWith({ name: 'scheduleUnitListModification', content: 'OK'});
                        expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
                    })
                )
            );
        });
    });

});
