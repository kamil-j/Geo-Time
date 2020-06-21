import {Injectable} from '@angular/core';
import {JhiAlertService} from 'ng-jhipster';
import {AuthServerProvider, JhiLanguageHelper} from '../shared';
import {DatePipe} from '@angular/common';
import * as $ from 'jquery';

@Injectable()
export class ScheduleInfoHelper {

    static SCHEDULE_INFO_EVENT_SOURCE_ID = 4;

    static hideEventPopover(element) {
        element.popover('hide');
        element.popover('disable');
    }

    static handleAfterAllRender() {
        $('body').on('click', (e) => {
            if (!$(e.target).parents().hasClass('fc-event') && !$(e.target).parents().hasClass('popover')) {
                (<any>$('.popover')).popover('hide');
            }
        });
    }

    static handleEventDestroy(event, element) {
        ScheduleInfoHelper.hideEventPopover(element);
    }

    constructor(
        private jhiAlertService: JhiAlertService,
        private authServerProvider: AuthServerProvider,
        private languageHelper: JhiLanguageHelper,
    ) {
    }

    getScheduleInfoEventSource(academicUnitId, academicUnitGroup) {
        return {
            id: ScheduleInfoHelper.SCHEDULE_INFO_EVENT_SOURCE_ID,
            url: '/api/schedule-info/' + academicUnitId,
            type: 'GET',
            data: {
                academicUnitGroup: academicUnitGroup ? academicUnitGroup.toString() : null,
            },
            startParam: 'startDate',
            endParam: 'endDate',
            error: () => this.handleFetchEventSourceError()
        };
    }

    private handleFetchEventSourceError() {
        this.jhiAlertService.error('geoTimeApp.scheduleInfo.fetchError', null, null);
    }

    handleEventRender(event, element) {
        const academicUnitGroup = event.academicUnitGroup ? ' | <i class="fa fa-users" aria-hidden="true"></i> '
            + this.languageHelper.getTranslatedString('geoTimeApp.AcademicUnitGroup.' + event.academicUnitGroup) : '';
        const translatedRoomText = this.languageHelper.getTranslatedString('geoTimeApp.scheduleInfo.event.room');

        const eventExtendedDescription = '<br/><span style="color: #97E4FF">' + translatedRoomText + ' ' + event.roomName
            + academicUnitGroup + '</span>';

        element.find('.fc-title').append(eventExtendedDescription);
    }

    handleAfterEventRender(event, element) {
        element.addClass('fc-event-' + event.roomName);

        if (event.source.id === ScheduleInfoHelper.SCHEDULE_INFO_EVENT_SOURCE_ID.toString() || event.source.id === undefined) {
            element.popover(this.getEventPopover(event));
        }
    }

    getEventPopover(event) {
        return {
            title: this.getPopoverTitleHtml(event),
            content: this.getPopoverContentHtml(event),
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
        const academicUnitGroup = event.academicUnitGroup ? '<div class="row col-12"><span>' +
            '<i class="fa fa-users" aria-hidden="true"></i> '
            + this.languageHelper.getTranslatedString('geoTimeApp.AcademicUnitGroup.' + event.academicUnitGroup)
            + '</span></div>' : '';

        return '<div class="container">' +
            '  <div class="row">' +
            '    <div class="row col-12">' +
            '      <span class="font-weight-bold">' + event.title + '</span>' +
            '    </div>' +
            '    <div class="row col-12">' +
            '      <span><i class="fa fa-clock-o" aria-hidden="true"></i> ' + startTimeString + ' - ' + endTimeString + '</span>' +
            '    </div>' +
            academicUnitGroup +
            '  </div>' +
            '</div>';
    }

    private getPopoverContentHtml(event) {
        const translatedTypeText = this.languageHelper.getTranslatedString('geoTimeApp.scheduleInfo.popover.type');
        const translatedRoomText = this.languageHelper.getTranslatedString('geoTimeApp.scheduleInfo.popover.room');
        const translatedLecturerText = this.languageHelper.getTranslatedString('geoTimeApp.scheduleInfo.popover.lecturer');
        const translatedFrequencyText = this.languageHelper.getTranslatedString('geoTimeApp.scheduleInfo.popover.frequency');
        const translatedFrequencyValue = this.languageHelper.getTranslatedString('geoTimeApp.ClassFrequency.' + event.frequency);

        return '<div class="container">' +
            '  <div class="row">' +
            '    <div class="row col-12">' +
            '      <span>' + translatedTypeText + ': ' + event.type + '</span>' +
            '    </div>' +
            '    <div class="row col-12">' +
            '      <span>' + translatedRoomText + ': ' + event.roomName + '</span>' +
            '    </div>' +
            '    <div class="row col-12">' +
            '      <span>' + translatedLecturerText + ': ' + event.lecturerName + '</span>' +
            '    </div>' +
            '    <div class="row col-12">' +
            '      <span>' + translatedFrequencyText + ': ' + translatedFrequencyValue + '</span>' +
            '    </div>' +
            '  </div>' +
            '</div>';
    }
}
