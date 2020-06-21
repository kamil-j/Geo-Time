/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable } from 'rxjs/Observable';
import { JhiEventManager } from 'ng-jhipster';

import { GeoTimeTestModule } from '../../../test.module';
import { ClassUnitGroupDeleteDialogComponent } from '../../../../../../main/webapp/app/entities/class-unit-group/class-unit-group-delete-dialog.component';
import { ClassUnitGroupService } from '../../../../../../main/webapp/app/entities/class-unit-group/class-unit-group.service';

describe('Component Tests', () => {

    describe('ClassUnitGroup Management Delete Component', () => {
        let comp: ClassUnitGroupDeleteDialogComponent;
        let fixture: ComponentFixture<ClassUnitGroupDeleteDialogComponent>;
        let service: ClassUnitGroupService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [GeoTimeTestModule],
                declarations: [ClassUnitGroupDeleteDialogComponent],
                providers: [
                    ClassUnitGroupService
                ]
            })
            .overrideTemplate(ClassUnitGroupDeleteDialogComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(ClassUnitGroupDeleteDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(ClassUnitGroupService);
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
