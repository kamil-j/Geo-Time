/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable } from 'rxjs/Observable';
import { JhiEventManager } from 'ng-jhipster';

import { GeoTimeTestModule } from '../../../test.module';
import { SchedulingTimeFrameDeleteDialogComponent } from '../../../../../../main/webapp/app/entities/scheduling-time-frame/scheduling-time-frame-delete-dialog.component';
import { SchedulingTimeFrameService } from '../../../../../../main/webapp/app/entities/scheduling-time-frame/scheduling-time-frame.service';

describe('Component Tests', () => {

    describe('SchedulingTimeFrame Management Delete Component', () => {
        let comp: SchedulingTimeFrameDeleteDialogComponent;
        let fixture: ComponentFixture<SchedulingTimeFrameDeleteDialogComponent>;
        let service: SchedulingTimeFrameService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [GeoTimeTestModule],
                declarations: [SchedulingTimeFrameDeleteDialogComponent],
                providers: [
                    SchedulingTimeFrameService
                ]
            })
            .overrideTemplate(SchedulingTimeFrameDeleteDialogComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(SchedulingTimeFrameDeleteDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(SchedulingTimeFrameService);
            mockEventManager = fixture.debugElement.injector.get(JhiEventManager);
            mockActiveModal = fixture.debugElement.injector.get(NgbActiveModal);
        });

        describe('confirmDelete', () => {
            it('Should call delete service on confirmDelete',
                inject([],
                    fakeAsync(() => {
                        // GIVEN
                        spyOn(service, 'delete').and.returnValue(Observable.of({}));

                        // WHEN
                        comp.confirmDelete(123);
                        tick();

                        // THEN
                        expect(service.delete).toHaveBeenCalledWith(123);
                        expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
                        expect(mockEventManager.broadcastSpy).toHaveBeenCalled();
                    })
                )
            );
        });
    });

});
