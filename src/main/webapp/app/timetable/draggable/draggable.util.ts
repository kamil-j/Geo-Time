import * as $ from 'jquery';
import {TouchUtil, ClassFrequency, SemesterHalf, WeekType} from '../../shared';
import {TimetableUtil} from '../timetable-util';

export class DraggableUtil {

    static hideSidebar(element) {
        if (TouchUtil.isTouchDevice()) {
            const sidebar = <any>$('#sidebar');
            sidebar.css({visibility: 'hidden'});
            sidebar.find('.external-event').css({visibility: 'hidden'});
            element.css({visibility: 'visible'});
        }
    }

    static showSidebar() {
        if (TouchUtil.isTouchDevice()) {
            const sidebar = <any>$('#sidebar');
            sidebar.css({visibility: 'visible'});
            sidebar.find('.external-event').css({visibility: 'visible'});
        }
    }

    static showBackgroundEvents(eventData, timetableView) {
        const classUnitGroup = eventData.classUnitGroupId ? eventData.classUnitGroupId : 'empty';

        TimetableUtil.showRoomBgEvents(eventData.selectedRoomId, classUnitGroup, eventData.frequency,
            eventData.onlySemesterHalf, timetableView);
        TimetableUtil.showAcademicUnitBgEvents(eventData.academicUnitId, eventData.academicUnitGroup, classUnitGroup,
            eventData.frequency, eventData.onlySemesterHalf, timetableView);
        if (eventData.relatedAcademicUnitGroups) {
            TimetableUtil.showRelatedAcademicUnitGroupsBgEvents(eventData.relatedAcademicUnitGroups, classUnitGroup,
                eventData.frequency, eventData.onlySemesterHalf, timetableView);
        }
        if (eventData.relatedUserIds) {
            TimetableUtil.showRelatedUsersBgEvents(eventData.relatedUserIds, classUnitGroup, eventData.frequency,
                eventData.onlySemesterHalf, timetableView);
        }
    }

    static getEventDurationFromMinutes(minutes: number) {
        const daysVal = Math.floor(Math.floor(minutes / 24) / 60);
        const hoursVal = Math.floor(Math.floor(minutes / 60) % 24);
        const minutesVal =  Math.floor(minutes % 60);

        return {
            days: daysVal,
            hours: hoursVal,
            minutes: minutesVal
        };
    }

    static addEventSourcesToCalendar(calendar, eventSources) {
        eventSources.forEach((eventSource) => {
            calendar.fullCalendar('addEventSource', eventSource);
        });
    }

    static removeEventSourceFromCalendar(calendar, eventSourceIds) {
        calendar.fullCalendar('removeEventSources', eventSourceIds);
    }

    static adjustPropertiesToTimetableView(draggableElement, eventData, timetableView) {
        if (DraggableUtil.isDisabled(eventData, timetableView)) {
            draggableElement.draggable('disable');
            draggableElement.addClass('disabled');
        } else {
            draggableElement.draggable('enable');
            draggableElement.removeClass('disabled');
        }
    }

    static isDisabled(eventData, timetableView) {
        const isNotProperWeek = (eventData.frequency === ClassFrequency.EVERY_WEEK && timetableView.weekType === WeekType.B);
        const isNotProperSemester = (!eventData.onlySemesterHalf && timetableView.semesterHalf === SemesterHalf.HALF2);
        return isNotProperWeek || isNotProperSemester;
    }
}
