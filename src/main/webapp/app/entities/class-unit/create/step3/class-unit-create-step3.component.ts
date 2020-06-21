import {Component, Input, ViewChild} from '@angular/core';

import {HttpErrorResponse, HttpResponse} from '@angular/common/http';
import {JhiAlertService} from 'ng-jhipster';
import {ClassUnitCreate, Room, Subdepartment, User, UserService} from '../../../../shared';
import {NgForm} from '@angular/forms';
import {RoomService} from '../../../room';
import {SubdepartmentService} from '../../../subdepartment';

@Component({
    selector: 'jhi-class-unit-create-step3',
    templateUrl: './class-unit-create-step3.component.html',
    styleUrls: [
        'class-unit-create-step3.css'
    ],
})
export class ClassUnitCreateStep3Component {

    @Input() classUnit: ClassUnitCreate;
    @ViewChild('form') form: NgForm;

    selectUserData: any[];
    selectRoomData: any[];
    subdepartments: Subdepartment[];
    assignSelectionType = 'subdepartment';

    constructor(
        private jhiAlertService: JhiAlertService,
        private userService: UserService,
        private subdepartmentService: SubdepartmentService,
        private roomService: RoomService,
    ) {
        this.selectRoomData = [];
    }

    clearValues() {
        this.classUnit.userId = null;
        this.classUnit.subdepartmentId = null;
    }

    load() {
        this.subdepartmentService.query()
            .subscribe((res: HttpResponse<Subdepartment[]>) => { this.subdepartments = res.body; }, (res: HttpErrorResponse) => this.onError(res.message));
        this.userService.query({size: 1000})
            .subscribe((res: HttpResponse<User[]>) => this.onUserSuccess(res), (res: HttpErrorResponse) => this.onError(res.message));
        this.roomService.query({size: 1000})
            .subscribe((res: HttpResponse<Room[]>) => this.onRoomSuccess(res), (res: HttpErrorResponse) => this.onError(res.message));
    }

    private onError(error: any) {
        this.jhiAlertService.error(error.message, null, null);
    }

    private onUserSuccess(response) {
        this.selectUserData = response.body.map((user) => {
            return {
                id: user.id.toString(),
                text: user.lastName + ' ' + user.firstName
            };
        });
    }

    private onRoomSuccess(response) {
        this.selectRoomData = response.body.map((room) => {
            return {
                id: room.id.toString(),
                text: room.name
            };
        });
    }

    trackSubdepartmentById(index: number, item: Subdepartment) {
        return item.id;
    }
}
