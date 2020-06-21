import {Directive, ElementRef, HostListener, Input, OnChanges, OnInit} from '@angular/core';
import * as $ from 'jquery';
import {TimetableHelper} from '../timetable.helper';
import {DraggableUtil} from './draggable.util';
import {TimetableView} from '../timetable-view.model';
import {TimetableUtil} from '../timetable-util';

@Directive({
    selector: '[jhiDraggable]'
})
export class DraggableDirective implements OnChanges, OnInit {

    calendar: any;
    @Input() data: any;
    @Input() user: any;
    @Input() canUserModifySchedule: boolean;
    @Input() timetableView: TimetableView;

    private isDisabled = false;

    constructor(
        private el: ElementRef,
        private timetableHelper: TimetableHelper
    ) {
    }

    @HostListener('change') onChanges() {
        (<any>jQuery(this.el.nativeElement)).data('event', this.getEventDataObject());
    }

    ngOnInit() {
        const draggableElement = (<any>jQuery(this.el.nativeElement));

        if (this.shouldBeDisabled()) {
            this.isDisabled = true;
            draggableElement.addClass('disabled');
            return;
        }

        draggableElement.draggable(this.getDragAndDropConfiguration());
        draggableElement.data('event', this.getEventDataObject());

        DraggableUtil.adjustPropertiesToTimetableView(draggableElement, this.data, this.timetableView);

        this.calendar = <any>$('#calendar');
    }

    private shouldBeDisabled() {
        return !this.data.rooms || this.data.rooms.length === 0;
    }

    ngOnChanges(changes: any) {
        if (changes.data && !changes.data.firstChange) {
            (<any>jQuery(this.el.nativeElement)).data('event', this.getEventDataObject());
        }
        if (!this.isDisabled && changes.timetableView && !changes.timetableView.firstChange) {
            const draggableElement = (<any>jQuery(this.el.nativeElement));
            DraggableUtil.adjustPropertiesToTimetableView(draggableElement, this.data, this.timetableView);
        }
    }

    getDragAndDropConfiguration() {
        return {
            start: ( event, element ) => this.handleEventStartDragging(event, element),
            stop: ( event, element ) => this.handleEventStopDragging(event, element),
            zIndex: 999,
            revert: true,
            revertDuration: 0,
            containment: '#container-timetable'
        };
    }

    handleEventStartDragging(event, element) {
        element.helper.addClass('drag-start');
        DraggableUtil.hideSidebar(element.helper);

        if (this.canUserModifySchedule) {
            const bgEventSources = this.timetableHelper.getExternalEventBgEventSources(this.user.id, this.data,
                this.timetableView.semesterHalf, this.timetableView.weekType);

            DraggableUtil.addEventSourcesToCalendar(this.calendar, bgEventSources);
            DraggableUtil.showBackgroundEvents(this.data, this.timetableView);
        }
    }

    handleEventStopDragging(event, element) {
        element.helper.removeClass('drag-start');
        DraggableUtil.showSidebar();

        if (this.canUserModifySchedule) {
            TimetableUtil.hideBackgroundEvents();
            DraggableUtil.removeEventSourceFromCalendar(this.calendar, [
                TimetableUtil.ROOM_BG_EXTERNAL_EVENT_SOURCE_ID,
                TimetableUtil.ACADEMIC_UNIT_BG_EXTERNAL_EVENT_SOURCE_ID,
                TimetableUtil.USERS_BG_EXTERNAL_EVENT_SOURCE_ID
            ]);
        }
    }

    getEventDataObject() {
        return {
            id: this.data.id,
            title: this.data.title,
            duration: DraggableUtil.getEventDurationFromMinutes(this.data.duration),
            roomId: this.data.selectedRoomId,
            academicUnitId: this.data.academicUnitId,
            academicUnitGroup: this.data.academicUnitGroup,
            classUnitGroupId: this.data.classUnitGroupId,
            lecturerId: this.data.userId,
            relatedAcademicUnitGroups: this.data.relatedAcademicUnitGroups,
            relatedUserIds: this.data.relatedUserIds,
            stick: false,
            type: this.data.classTypeName,
            roomName: this.getSelectedRoomName(),
            frequency: this.data.frequency
        };
    }

    getSelectedRoomName() {
        const selectedRoom = this.data.rooms.find((room) => room.id.toString() === this.data.selectedRoomId.toString());
        return selectedRoom.name;
    }
}
