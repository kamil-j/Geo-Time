/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable } from 'rxjs/Observable';
import { JhiEventManager } from 'ng-jhipster';

import { GeoTimeTestModule } from '../../../test.module';
import { ScheduleUnitDeleteDialogComponent } from '../../../../../../main/webapp/app/entities/schedule-unit/schedule-unit-delete-dialog.component';
import { ScheduleUnitService } from '../../../../../../main/webapp/app/entities/schedule-unit/schedule-unit.service';

describe('Component Tests', () => {

    describe('ScheduleUnit Management Delete Component', () => {
        let comp: ScheduleUnitDeleteDialogComponent;
        let fixture: ComponentFixture<ScheduleUnitDeleteDialogComponent>;
        let service: ScheduleUnitService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [GeoTimeTestModule],
                declarations: [ScheduleUnitDeleteDialogComponent],
                providers: [
                    ScheduleUnitService
                ]
            })
            .overrideTemplate(ScheduleUnitDeleteDialogComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(ScheduleUnitDeleteDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(ScheduleUnitService);
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
