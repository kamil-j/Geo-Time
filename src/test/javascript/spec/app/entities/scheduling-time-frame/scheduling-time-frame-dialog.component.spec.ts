/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async, inject, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable } from 'rxjs/Observable';
import { JhiEventManager } from 'ng-jhipster';

import { GeoTimeTestModule } from '../../../test.module';
import { SchedulingTimeFrameDialogComponent } from '../../../../../../main/webapp/app/entities/scheduling-time-frame/scheduling-time-frame-dialog.component';
import { SchedulingTimeFrameService } from '../../../../../../main/webapp/app/entities/scheduling-time-frame/scheduling-time-frame.service';
import { SchedulingTimeFrame } from '../../../../../../main/webapp/app/shared/model/scheduling-time-frame.model';
import { UserGroupService } from '../../../../../../main/webapp/app/entities/user-group';
import { SubdepartmentService } from '../../../../../../main/webapp/app/entities/subdepartment';
import { SemesterService } from '../../../../../../main/webapp/app/entities/semester';

describe('Component Tests', () => {

    describe('SchedulingTimeFrame Management Dialog Component', () => {
        let comp: SchedulingTimeFrameDialogComponent;
        let fixture: ComponentFixture<SchedulingTimeFrameDialogComponent>;
        let service: SchedulingTimeFrameService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [GeoTimeTestModule],
                declarations: [SchedulingTimeFrameDialogComponent],
                providers: [
                    UserGroupService,
                    SubdepartmentService,
                    SemesterService,
                    SchedulingTimeFrameService
                ]
            })
            .overrideTemplate(SchedulingTimeFrameDialogComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(SchedulingTimeFrameDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(SchedulingTimeFrameService);
            mockEventManager = fixture.debugElement.injector.get(JhiEventManager);
            mockActiveModal = fixture.debugElement.injector.get(NgbActiveModal);
        });

        describe('save', () => {
            it('Should call update service on save for existing entity',
                inject([],
                    fakeAsync(() => {
                        // GIVEN
                        const entity = new SchedulingTimeFrame(123);
                        spyOn(service, 'update').and.returnValue(Observable.of(new HttpResponse({body: entity})));
                        comp.schedulingTimeFrame = entity;
                        // WHEN
                        comp.save();
                        tick(); // simulate async

                        // THEN
                        expect(service.update).toHaveBeenCalledWith(entity);
                        expect(comp.isSaving).toEqual(false);
                        expect(mockEventManager.broadcastSpy).toHaveBeenCalledWith({ name: 'schedulingTimeFrameListModification', content: 'OK'});
                        expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
                    })
                )
            );

            it('Should call create service on save for new entity',
                inject([],
                    fakeAsync(() => {
                        // GIVEN
                        const entity = new SchedulingTimeFrame();
                        spyOn(service, 'create').and.returnValue(Observable.of(new HttpResponse({body: entity})));
                        comp.schedulingTimeFrame = entity;
                        // WHEN
                        comp.save();
                        tick(); // simulate async

                        // THEN
                        expect(service.create).toHaveBeenCalledWith(entity);
                        expect(comp.isSaving).toEqual(false);
                        expect(mockEventManager.broadcastSpy).toHaveBeenCalledWith({ name: 'schedulingTimeFrameListModification', content: 'OK'});
                        expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
                    })
                )
            );
        });
    });

});
