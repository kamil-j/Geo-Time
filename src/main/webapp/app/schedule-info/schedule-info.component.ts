import {Component, OnDestroy, OnInit} from '@angular/core';
import * as $ from 'jquery';
import 'fullcalendar';
import {JhiAlertService, JhiLanguageService} from 'ng-jhipster';
import {Subscription} from 'rxjs/Subscription';
import {ActivatedRoute} from '@angular/router';
import {HttpErrorResponse, HttpResponse} from '@angular/common/http';
import {TranslateService} from '@ngx-translate/core';
import {ScheduleInfoHelper} from './schedule-info.helper';
import {SemesterInfo, SemesterInfoService, AcademicUnitInfo,
    AcademicUnitInfoService, AcademicUnitGroupsInfo} from '../shared';

@Component({
    selector: 'jhi-schedule-info',
    templateUrl: './schedule-info.component.html',
    styleUrls: [
        'schedule-info.css'
    ]
})
export class ScheduleInfoComponent implements OnInit, OnDestroy {

    calendar: any;
    availableDegrees: any;
    availableYears: any;
    availableGroups: any;
    selectedDegree: any;
    selectedYear: any;
    selectedGroup: any;
    selectedAcademicUnit: AcademicUnitInfo;

    private currentSemesterInfo: SemesterInfo;
    private academicUnitsInfo: AcademicUnitInfo[];
    private subscription: Subscription;

    constructor(
        private jhiAlertService: JhiAlertService,
        private languageService: JhiLanguageService,
        private translateService: TranslateService,
        private semesterInfoService: SemesterInfoService,
        private academicUnitInfoService: AcademicUnitInfoService,
        private route: ActivatedRoute,
        private scheduleInfoHelper: ScheduleInfoHelper
    ) {
    }

    ngOnInit() {
        this.calendar = <any>$('#calendar');
        this.getCurrentSemesterInfo();
        this.subscription = this.route.params.subscribe((params) => {
            this.resetFilterOptions();
            this.loadAcademicUnitsInfo(params['id']);
        });
    }

    private getCurrentSemesterInfo() {
        this.semesterInfoService.getCurrentSemester().subscribe((res: HttpResponse<SemesterInfo>) => {
            this.currentSemesterInfo = res.body;
            this.loadCalendar();
        }, () => this.loadCalendar());
    }

    private resetFilterOptions() {
        this.selectedDegree = undefined;
        this.selectedYear = undefined;
        this.selectedGroup = undefined;
        this.availableDegrees = undefined;
        this.availableYears = undefined;
        this.availableGroups = undefined;
        this.selectedAcademicUnit = undefined;
    }

    private loadAcademicUnitsInfo(id) {
        this.academicUnitInfoService.query({'studyFieldId.equals': id})
            .subscribe((res: HttpResponse<AcademicUnitInfo[]>) => this.onLoadAcademicUnitsInfoSuccess(res.body),
                (res: HttpErrorResponse) => this.onLoadAcademicUnitsInfoError(res.message)
            );
    }

    private onLoadAcademicUnitsInfoSuccess(data) {
        this.academicUnitsInfo = data;
        this.availableDegrees = this.academicUnitsInfo.map((value) => value.degree)
            .filter((x, i, a) => a.indexOf(x) === i);
    }

    private onLoadAcademicUnitsInfoError(error) {
        this.jhiAlertService.error(error.message, null, null);
    }

    degreeSelectChange() {
        this.selectedYear = undefined;
        this.selectedGroup = undefined;
        this.availableGroups = undefined;
        this.availableYears = this.academicUnitsInfo
            .filter((value) => value.degree === this.selectedDegree)
            .map((value) => value.year)
            .filter((x, i, a) => a.indexOf(x) === i);
    }

    yearSelectChange() {
        this.selectedAcademicUnit = this.academicUnitsInfo.find(
            (value) => value.degree === this.selectedDegree && value.year === this.selectedYear
        );
        this.loadEventsToCalendar();
        this.selectedGroup = undefined;
        this.loadAcademicUnitGroups();
    }

    private loadAcademicUnitGroups() {
        this.academicUnitInfoService.findGroups(this.selectedAcademicUnit.id).subscribe(
            (res: HttpResponse<AcademicUnitGroupsInfo>) => this.availableGroups = res.body.academicUnitGroups
        );
    }

    groupSelectChange() {
        this.loadEventsToCalendar();
    }

    private loadCalendar() {
        this.calendar.fullCalendar({
            header: {
                left: 'prev,next today',
                center: 'title',
                right: 'month,agendaWeek,agendaDay'
            },
            weekends: false,
            defaultView: 'agendaWeek',
            nowIndicator: true,
            validRange: {
                start: this.currentSemesterInfo != null ? this.currentSemesterInfo.start : null,
                end: this.currentSemesterInfo != null ? this.currentSemesterInfo.end : null
            },
            allDaySlot: false,
            minTime: '08:00:00',
            maxTime: '22:00:00',
            timeFormat: 'H:mm',
            slotLabelFormat: 'H:mm',
            slotLabelInterval: '01:00',
            height: 'auto',
            firstDay: 1,
            navLinks: true,
            editable: false,
            eventLimit: true,
            themeSystem: 'bootstrap4',
            eventColor: '#17a2b8',
            eventRender: (event, element) => this.scheduleInfoHelper.handleEventRender(event, element),
            eventAfterRender: (event, element) => this.scheduleInfoHelper.handleAfterEventRender(event, element),
            eventAfterAllRender: () => ScheduleInfoHelper.handleAfterAllRender(),
            eventDestroy: (event, element) => ScheduleInfoHelper.handleEventDestroy(event, element),
        });

        this.updateCurrentCalendarLanguage();
    }

    private loadEventsToCalendar() {
        const calendar = <any>$('#calendar');
        calendar.fullCalendar('removeEventSources');
        calendar.fullCalendar('addEventSource', this.scheduleInfoHelper.getScheduleInfoEventSource(this.selectedAcademicUnit.id, this.selectedGroup));
    }

    updateCurrentCalendarLanguage() {
        this.languageService.getCurrent().then((language) => {
            this.calendar.fullCalendar('option', 'locale', language);
        });
        this.setCalendarI18nHook();
    }

    private setCalendarI18nHook() {
        this.translateService.onLangChange.subscribe(() => {
            this.calendar.fullCalendar('option', 'locale', this.translateService.currentLang);
        });
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }
}
