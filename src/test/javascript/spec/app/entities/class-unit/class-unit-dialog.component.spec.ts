/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async, inject, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable } from 'rxjs/Observable';
import { JhiEventManager } from 'ng-jhipster';

import { GeoTimeTestModule } from '../../../test.module';
import { ClassUnitDialogComponent } from '../../../../../../main/webapp/app/entities/class-unit/class-unit-dialog.component';
import { ClassUnitService } from '../../../../../../main/webapp/app/entities/class-unit/class-unit.service';
import { ClassUnit } from '../../../../../../main/webapp/app/shared/model/class-unit.model';
import { ClassTypeService } from '../../../../../../main/webapp/app/entities/class-type';
import { UserService } from '../../../../../../main/webapp/app/shared';
import { RoomService } from '../../../../../../main/webapp/app/entities/room';
import { BookingUnitService } from '../../../../../../main/webapp/app/entities/booking-unit';
import { SemesterService } from '../../../../../../main/webapp/app/entities/semester';
import { AcademicUnitService } from '../../../../../../main/webapp/app/entities/academic-unit';
import { ClassUnitGroupService } from '../../../../../../main/webapp/app/entities/class-unit-group';
import { SubdepartmentService } from '../../../../../../main/webapp/app/entities/subdepartment';

describe('Component Tests', () => {

    describe('ClassUnit Management Dialog Component', () => {
        let comp: ClassUnitDialogComponent;
        let fixture: ComponentFixture<ClassUnitDialogComponent>;
        let service: ClassUnitService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [GeoTimeTestModule],
                declarations: [ClassUnitDialogComponent],
                providers: [
                    ClassTypeService,
                    UserService,
                    RoomService,
                    BookingUnitService,
                    SemesterService,
                    AcademicUnitService,
                    ClassUnitGroupService,
                    SubdepartmentService,
                    ClassUnitService
                ]
            })
            .overrideTemplate(ClassUnitDialogComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(ClassUnitDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(ClassUnitService);
            mockEventManager = fixture.debugElement.injector.get(JhiEventManager);
            mockActiveModal = fixture.debugElement.injector.get(NgbActiveModal);
        });

        describe('save', () => {
            it('Should call update service on save for existing entity',
                inject([],
                    fakeAsync(() => {
                        // GIVEN
                        const entity = new ClassUnit(123);
                        spyOn(service, 'update').and.returnValue(Observable.of(new HttpResponse({body: entity})));
                        comp.classUnit = entity;
                        // WHEN
                        comp.save();
                        tick(); // simulate async

                        // THEN
                        expect(service.update).toHaveBeenCalledWith(entity);
                        expect(comp.isSaving).toEqual(false);
                        expect(mockEventManager.broadcastSpy).toHaveBeenCalledWith({ name: 'classUnitListModification', content: 'OK'});
                        expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
                    })
                )
            );

            it('Should call create service on save for new entity',
                inject([],
                    fakeAsync(() => {
                        // GIVEN
                        const entity = new ClassUnit();
                        spyOn(service, 'create').and.returnValue(Observable.of(new HttpResponse({body: entity})));
                        comp.classUnit = entity;
                        // WHEN
                        comp.save();
                        tick(); // simulate async

                        // THEN
                        expect(service.create).toHaveBeenCalledWith(entity);
                        expect(comp.isSaving).toEqual(false);
                        expect(mockEventManager.broadcastSpy).toHaveBeenCalledWith({ name: 'classUnitListModification', content: 'OK'});
                        expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
                    })
                )
            );
        });
    });

});
