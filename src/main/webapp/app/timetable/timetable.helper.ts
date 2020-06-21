import {Injectable} from '@angular/core';
import {JhiAlertService} from 'ng-jhipster';
import {AcademicUnitGroupUtil, AuthServerProvider, JhiLanguageHelper, SemesterHalf, WeekType, ClassFrequency} from '../shared';
import {DatePipe} from '@angular/common';
import * as $ from 'jquery';
import {TimetableUtil} from './timetable-util';
import {TimetableView} from './timetable-view.model';

@Injectable()
export class TimetableHelper {

    constructor(
        private jhiAlertService: JhiAlertService,
        private authServerProvider: AuthServerProvider,
        private languageHelper: JhiLanguageHelper,
    ) {
    }

    getBookingsSource(userId, semesterHalf, weekType) {
        return {
            id: TimetableUtil.BOOKING_EVENT_SOURCE_ID,
            url: '/api/timetable-bookings',
            type: 'GET',
            headers: {
                Authorization: 'Bearer ' + this.authServerProvider.getToken(),
            },
            data: {
                userId,
                semesterHalf,
                weekType
            },
            startParam: '',
            endParam: '',
            timezone: '',
            error: () => this.handleFetchEventSourceError()
        };
    }

    getRoomsBgEventSource(userId, semesterHalf, weekType) {
        return {
            id: TimetableUtil.ROOMS_BG_EVENT_SOURCE_ID,
            url: '/api/booking-conflicts/room',
            type: 'GET',
            headers: {
                Authorization: 'Bearer ' + this.authServerProvider.getToken(),
            },
            data: {
                semesterHalf,
                weekType,
                frequency: ClassFrequency.EVERY_WEEK,
                userId
            },
            startParam: '',
            endParam: '',
            timezone: '',
            overlap: true,
            rendering: 'background',
            color: '#8100d5',
            error: () => this.handleFetchEventSourceError()
        };
    }

    getAcademicUnitsBgEventSource(userId, semesterHalf, weekType) {
        return {
            id: TimetableUtil.ACADEMIC_UNITS_BG_EVENT_SOURCE_ID,
            url: '/api/booking-conflicts/academicUnit',
            type: 'GET',
            headers: {
                Authorization: 'Bearer ' + this.authServerProvider.getToken(),
            },
            data: {
                semesterHalf,
                weekType,
                frequency: ClassFrequency.EVERY_WEEK,
                userId
            },
            startParam: '',
            endParam: '',
            timezone: '',
            overlap: true,
            rendering: 'background',
            color: '#ff0e0e',
            error: () => this.handleFetchEventSourceError()
        };
    }

    getUsersBgEventSource(userId, semesterHalf, weekType) {
        return {
            id: TimetableUtil.USERS_BG_EVENT_SOURCE_ID,
            url: '/api/booking-conflicts/user',
            type: 'GET',
            headers: {
                Authorization: 'Bearer ' + this.authServerProvider.getToken(),
            },
            data: {
                semesterHalf,
                weekType,
                frequency: ClassFrequency.EVERY_WEEK,
                userId
            },
            startParam: '',
            endParam: '',
            timezone: '',
            overlap: true,
            rendering: 'background',
            color: '#ff0e0e',
            error: () => this.handleFetchEventSourceError()
        };
    }

    getExternalEventBgEventSources(userId, eventData, semesterHalf, weekType) {
        const roomBgExternalEventSource = this.getRoomBgExternalEventSource(userId, eventData.selectedRoomId,
            semesterHalf, weekType, eventData.frequency);
        const academicUnitBgExternalEventSource = this.getAcademicUnitBgExternalEventSource(userId, eventData.id,
            semesterHalf, weekType, eventData.frequency);

        if (!eventData.relatedUserIds || eventData.relatedUserIds.length === 0) {
            return [roomBgExternalEventSource, academicUnitBgExternalEventSource];
        }

        const relatedUsersBgExternalEventSource = this.getRelatedUsersBgExternalEventSource(userId, eventData.relatedUserIds,
            semesterHalf, weekType, eventData.frequency);

        return [roomBgExternalEventSource, academicUnitBgExternalEventSource, relatedUsersBgExternalEventSource];
    }

    private getRoomBgExternalEventSource(userId, roomId, semesterHalf, weekType, frequency) {
        return {
            id: TimetableUtil.ROOM_BG_EXTERNAL_EVENT_SOURCE_ID,
            url: '/api/booking-conflicts/room',
            type: 'GET',
            headers: {
                Authorization: 'Bearer ' + this.authServerProvider.getToken(),
            },
            data: {
                semesterHalf,
                weekType,
                frequency,
                roomId,
                userId
            },
            startParam: '',
            endParam: '',
            timezone: '',
            overlap: true,
            rendering: 'background',
            color: '#8100d5',
            error: () => this.handleFetchEventSourceError()
        };
    }

    private getAcademicUnitBgExternalEventSource(userId, classUnitId, semesterHalf, weekType, frequency) {
        return {
            id: TimetableUtil.ACADEMIC_UNIT_BG_EXTERNAL_EVENT_SOURCE_ID,
            url: '/api/booking-conflicts/academicUnit',
            type: 'GET',
            headers: {
                Authorization: 'Bearer ' + this.authServerProvider.getToken(),
            },
            data: {
                semesterHalf,
                weekType,
                frequency,
                classUnitId,
                userId
            },
            startParam: '',
            endParam: '',
            timezone: '',
            overlap: true,
            rendering: 'background',
            color: '#ff0e0e',
            error: () => this.handleFetchEventSourceError()
        };
    }

    private getRelatedUsersBgExternalEventSource(userId, relatedUserIds, semesterHalf, weekType, frequency) {
        return {
            id: TimetableUtil.USERS_BG_EXTERNAL_EVENT_SOURCE_ID,
            url: '/api/booking-conflicts/user',
            type: 'GET',
            headers: {
                Authorization: 'Bearer ' + this.authServerProvider.getToken(),
            },
            data: {
                semesterHalf,
                weekType,
                frequency,
                userIds: relatedUserIds.toString(),
                userId
            },
            startParam: '',
            endParam: '',
            timezone: '',
            overlap: true,
            rendering: 'background',
            color: '#ff0e0e',
            error: () => this.handleFetchEventSourceError()
        };
    }

    private handleFetchEventSourceError() {
        this.jhiAlertService.error('geoTimeApp.timetable.fetchError', null, null); // TODO: Probably translation not found (check 57 insted error: '() => method()' put 'method()'
    }

    handleEventRender(event, element) {
        const translatedRoomText = this.languageHelper.getTranslatedString('geoTimeApp.scheduleInfo.event.room');
        const eventExtendedDescription = '<br/><span style="color: #97E4FF">' + translatedRoomText + ' ' + event.roomName + '</span>';

        element.find('.fc-title').append(eventExtendedDescription);
    }

    adjustTimetableView(timetableView: TimetableView) {
        const prevButton = $('.fc-customPrev-button');
        prevButton.html('<span class="fa fa-chevron-left"></span>');

        const nextButton = $('.fc-customNext-button');
        nextButton.html('<span class="fa fa-chevron-right"></span>');

        const semesterHalf = timetableView.semesterHalf;
        const weekType = timetableView.weekType;

        if (semesterHalf === SemesterHalf.HALF1 && weekType === WeekType.A) {
            prevButton.prop('disabled', true);
        } else if (semesterHalf === SemesterHalf.HALF1 && weekType === WeekType.B) {
            prevButton.prop('disabled', false);
        } else if (semesterHalf === SemesterHalf.HALF2 && weekType === WeekType.A) {
            nextButton.prop('disabled', false);
        } else if (semesterHalf === SemesterHalf.HALF2 && weekType === WeekType.B) {
            nextButton.prop('disabled', true);
        }

        $('#calendar .fc-toolbar .fc-center h2').text(
            this.languageHelper.getTranslatedString('geoTimeApp.timetable.title.' + semesterHalf + '.' + weekType)
        );
    }

    handleAfterEventRender(event, element, managementViewMode) {
        element.addClass('fc-event-r-' + event.roomId);
        element.addClass('fc-event-au-' + event.academicUnitId);
        element.addClass('fc-event-aug-' + event.academicUnitGroup);
        element.addClass('fc-event-u-' + event.userId);
        element.addClass('fc-event-cug-' + event.classUnitGroupId);
        element.addClass('fc-event-wt-' + event.weekType);
        element.addClass('fc-event-sh-' + event.semesterHalf);

        if (event.source.id === TimetableUtil.BOOKING_EVENT_SOURCE_ID.toString() || event.source.id === undefined) {
            element.popover(this.getEventPopover(event, managementViewMode));
        }
        if (event.source.id === TimetableUtil.ROOMS_BG_EVENT_SOURCE_ID.toString() ||
            event.source.id === TimetableUtil.ACADEMIC_UNITS_BG_EVENT_SOURCE_ID.toString() ||
            event.source.id === TimetableUtil.USERS_BG_EVENT_SOURCE_ID.toString()) {
            element.css({display: 'none'});
        }
    }

    getEventPopover(event, managementViewMode) {
        return {
            title: this.getPopoverTitleHtml(event),
            content: this.getPopoverContentHtml(event, managementViewMode),
            html: true,
            trigger: 'click',
            placement: 'auto',
            container: 'body'
        };
    }

    private getPopoverTitleHtml(event) {
        const datePipe = new DatePipe('en');
        const startTimeString = datePipe.transform(event.start, 'HH:mm');
        const endTimeString = datePipe.transform(event.end, 'HH:mm');
        const lockIcon = event.locked ? '[<i class="fa fa-lock text-danger" aria-hidden="true"></i>] ' : '';
        const academicUnitGroup = event.academicUnitGroup ?
            '-' + AcademicUnitGroupUtil.convertToShortName(event.academicUnitGroup) : '';

        return '<div class="container">' +
            '  <div class="row">' +
            '    <div class="row col-12">' +
            '      <span class="font-weight-bold">' + lockIcon + event.title + '</span>' +
            '    </div>' +
            '    <div class="row col-12">' +
            '      <span><i class="fa fa-clock-o" aria-hidden="true"></i> ' + startTimeString + ' - ' + endTimeString + '</span>' +
            '    </div>' +
            '    <div class="row col-12">' +
            '       <span class="font-weight-bold"><i class="fa fa-graduation-cap" aria-hidden="true"></i>&nbsp;</span>' +
            '       <span class="text-info font-weight-bold">' + event.academicUnitName + '</span>' +
            '       <span class="text-secondary font-weight-bold">' + academicUnitGroup + '</span>' +
            '    </div>' +
            '  </div>' +
            '</div>';
    }

    private getPopoverContentHtml(event, managementViewMode) {
        const translatedTypeText = this.languageHelper.getTranslatedString('geoTimeApp.timetable.popover.type');
        const translatedRoomText = this.languageHelper.getTranslatedString('geoTimeApp.timetable.popover.room');
        const translatedFrequencyText = this.languageHelper.getTranslatedString('geoTimeApp.timetable.popover.frequency');
        const translatedFrequencyValue = this.languageHelper.getTranslatedString('geoTimeApp.ClassFrequency.' + event.frequency);
        const popoverButtons = managementViewMode ? this.getPopoverExtendedButtons(event) : this.getPopoverBasicButtons(event);

        return '<div class="container">' +
            '  <div class="row">' +
            '    <div class="row col-12">' +
            '      <span>' + translatedTypeText + ': ' + event.type + '</span>' +
            '    </div>' +
            '    <div class="row  col-12">' +
            '      <span>' + translatedRoomText + ': ' + event.roomName + '</span>' +
            '    </div>' +
            '    <div class="row  col-12">' +
            '      <span>' + translatedFrequencyText + ': ' + translatedFrequencyValue + '</span>' +
            '    </div>' +
            '  </div>' +
            popoverButtons +
            '</div>';
    }

    private getPopoverExtendedButtons(event) {
        const translatedCancelText = this.languageHelper.getTranslatedString('geoTimeApp.timetable.popover.cancel');
        const translatedLockEventBtnText = event.locked ? this.languageHelper.getTranslatedString('geoTimeApp.timetable.popover.unlock') :
            this.languageHelper.getTranslatedString('geoTimeApp.timetable.popover.lock');
        const lockEventBtnColorClass = event.locked ? 'btn-success' : 'btn-danger';
        const lockEventBtnIcon = event.locked ? 'fa-unlock' : 'fa-lock';

        return '<div class="row my-2 justify-content-end">' +
            '     <div class="btn-group" role="group" aria-label="Basic example">' +
            '        <button type="button" class="lockEventBtn btn ' + lockEventBtnColorClass + ' btn-sm" data-event-id="' + event.id + '" data-locked="' + event.locked + '">' +
            '           <i class="fa ' + lockEventBtnIcon + '" aria-hidden="true"></i> ' + translatedLockEventBtnText +
            '        </button>' +
            '        <a href="#cancelEventConfirmationModal" class="btn btn-secondary btn-sm" data-toggle="modal" data-event-id="' + event.id + '">' +
            '           <i class="fa fa-close" aria-hidden="true"></i> ' + translatedCancelText +
            '        </a>' +
            '     </div>' +
            '</div>';
    }

    private getPopoverBasicButtons(event) {
        if (event.locked) {
            return '';
        }
        const translatedCancelText = this.languageHelper.getTranslatedString('geoTimeApp.timetable.popover.cancel');
        return '<div class="row my-2 justify-content-end">'
            + '<a href="#cancelEventConfirmationModal" class="btn btn-secondary btn-sm" data-toggle="modal" data-event-id="'
            + event.id + '">' + translatedCancelText
            + '</a>' +
            '</div>';
    }
}
