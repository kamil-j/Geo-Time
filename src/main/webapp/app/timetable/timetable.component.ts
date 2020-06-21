import {Component, OnDestroy, OnInit, ViewEncapsulation} from '@angular/core';
import 'fullcalendar';
import * as $ from 'jquery';
import {JhiAlertService, JhiLanguageService} from 'ng-jhipster';
import {
    AcademicUnitGroupUtil,
    Booking,
    BookingService,
    Principal,
    TouchUtil,
    User,
    UserService,
    BookingLock,
    ClassUnit,
    SchedulingTimeFrame,
    SemesterHalf,
    WeekType,
    MANAGER_AUTHORITY
} from '../shared';
import {TimetableService} from './timetable.service';
import {HttpErrorResponse, HttpResponse} from '@angular/common/http';
import {SchedulingTimeFrameService} from '../entities/scheduling-time-frame';
import {TranslateService} from '@ngx-translate/core';
import {Subscription} from 'rxjs';
import {ActivatedRoute} from '@angular/router';
import {TimetableHelper} from './timetable.helper';
import {TimetableView} from './timetable-view.model';
import {TimetableUtil} from './timetable-util';
import {TimetableConf} from './timetable.conf';

@Component({
    selector: 'jhi-timetable',
    templateUrl: './timetable.component.html',
    styleUrls: [
        'timetable.css'
    ],
    encapsulation: ViewEncapsulation.None
})
export class TimetableComponent implements OnInit, OnDestroy {

    user: User;
    classUnits: ClassUnit[];
    schedulingTimeFrame: SchedulingTimeFrame;
    previousPage: any;
    itemsPerPage: any;
    page: any;
    totalItems: any;
    canUserModifySchedule = false;
    selectedEventId: number;
    calendar: any;
    timetableView: TimetableView;
    managementViewMode = false;
    isTouchDevice: boolean;

    private subscription: Subscription;

    constructor(
        private userService: UserService,
        private route: ActivatedRoute,
        private jhiAlertService: JhiAlertService,
        private languageService: JhiLanguageService,
        private translateService: TranslateService,
        private bookingService: BookingService,
        private timetableService: TimetableService,
        private timetableHelper: TimetableHelper,
        private schedulingTimeFrameService: SchedulingTimeFrameService,
        private principal: Principal,
    ) {
        this.isTouchDevice = TouchUtil.isTouchDevice();
        this.itemsPerPage = this.isTouchDevice ? 3 : 5;
        this.previousPage = 1;
        this.page = 1;
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            if (params['login']) {
                this.initializeComponentForUserByLogin(params['login']);
            } else {
                this.initializeComponentForCurrentUser();
            }
        });
    }

    initializeComponentForUserByLogin(login) {
        this.userService.find(login).subscribe((response) => {
            this.user = response.body;
            this.managementViewMode = true;
            this.initializeTimetable();
        });
    }

    initializeComponentForCurrentUser() {
        this.principal.identity().then((user) => {
            this.user = user;
            this.initializeTimetable();
        });
    }

    initializeTimetable() {
        this.timetableView = new TimetableView(SemesterHalf.HALF1, WeekType.A);
        this.calendar = <any>$('#calendar');
        this.initializeCalendar();
        this.initializeSchedulingTimeFrames();
        this.loadUserClassUnits();
        this.initializeCancelEventConfirmationModal();
        if (this.managementViewMode) {
            this.initializeLockBookingBtn();
        }
    }

    private initializeSchedulingTimeFrames() {
        if (!this.managementViewMode) {
            this.loadSchedulingTimeFrames();
            return;
        }
        this.principal.identity().then((user) => {
            if (user.authorities.includes(MANAGER_AUTHORITY)) {
                this.canUserModifySchedule = true;
                TimetableUtil.allowModifying(this.calendar);
            } else {
                this.loadSchedulingTimeFrames();
            }
        });
    }

    private loadSchedulingTimeFrames() {
        this.schedulingTimeFrameService.findForUser(this.user.id).subscribe((response) => {
            this.schedulingTimeFrame = response.body;
            this.checkIfUserCanModifySchedule();
        });
    }

    private checkIfUserCanModifySchedule() {
        if (!this.schedulingTimeFrame) {
            this.canUserModifySchedule = true;
        } else {
            const currentTime = new Date().getTime();
            this.canUserModifySchedule = this.schedulingTimeFrame.startTime.getTime() <= currentTime
                && currentTime < this.schedulingTimeFrame.endTime.getTime();
        }
        if (this.canUserModifySchedule) {
            TimetableUtil.allowModifying(this.calendar);
        }
    }

    loadUserClassUnits() {
        this.timetableService.query({
            page: this.page - 1,
            size: this.itemsPerPage,
            userId: this.user.id
        }).subscribe(
            (res: HttpResponse<ClassUnit[]>) => this.onLoadUserClassUnitsSuccess(res),
            (res: HttpErrorResponse) => this.jhiAlertService.error(res.error.message, null, null)
        );
    }

    loadUserClassUnitsPage(page: number) {
        if (page !== this.previousPage) {
            this.previousPage = page;
            this.loadUserClassUnits();
        }
    }

    private onLoadUserClassUnitsSuccess(res) {
        this.totalItems = res.headers.get('X-Total-Count');
        this.classUnits = res.body;
        this.classUnits.forEach((classUnit) => TimetableUtil.setDefaultRoom(classUnit));
    }

    initializeCalendar() {
        this.calendar.fullCalendar({
            ...TimetableConf.DEFAULT,
            customButtons: {
                customNext: { click: () => this.handleNextButtonClick() },
                customPrev: { click: () => this.handlePrevButtonClick() }
            },
            eventOverlap: (stateEvent, movingEvent) => TimetableUtil.isEventOverlapAllowed(stateEvent, movingEvent, this.timetableView),
            eventReceive: (event) => this.createBooking(event),
            eventSources: this.getCalendarEventSources(),
            eventDrop: (event, delta, revertFunc) => this.updateBooking(event, revertFunc),
            eventRender: (event, element) => this.timetableHelper.handleEventRender(event, element),
            eventAfterRender: (event, element) => this.timetableHelper.handleAfterEventRender(event, element, this.managementViewMode),
            eventAfterAllRender: () => {
                TimetableUtil.handleAfterAllRender();
                this.timetableHelper.adjustTimetableView(this.timetableView);
            },
            eventDragStart: (event) => TimetableUtil.handleEventStartDragging(event, this.timetableView),
            eventDragStop: () => TimetableUtil.handleEventStopDragging(),
            eventDestroy: (event, element) => TimetableUtil.handleEventDestroy(event, element),
        });

        this.calendar.fullCalendar('render');
        this.updateCurrentCalendarLanguage();
    }

    private handleNextButtonClick() {
        if (this.timetableView.hasNext()) {
            this.timetableView = this.timetableView.getNext();
            TimetableUtil.refetchEventSources(this.calendar);
            this.timetableHelper.adjustTimetableView(this.timetableView);
        }
    }

    private handlePrevButtonClick() {
        if (this.timetableView.hasPrev()) {
            this.timetableView = this.timetableView.getPrev();
            TimetableUtil.refetchEventSources(this.calendar);
            this.timetableHelper.adjustTimetableView(this.timetableView);
        }
    }

    createBooking(event) {
        const booking = new Booking(event.id, event.start, event.end, event.start.day() - 1, this.timetableView.weekType,
            this.timetableView.semesterHalf, event.roomId, this.user.id);

        if (event.classUnitGroupId != null) {
            this.removeClassUnitGroupFromClassUnitList(event.classUnitGroupId);
        } else {
            this.removeClassUnitFromClassUnitList(event.id);
        }

        this.bookingService.createBooking(booking).subscribe(
            (res: HttpResponse<Booking>) => this.onCreateBookingSuccess(),
            (res: HttpErrorResponse) => this.onCreateBookingError(res.error)
        );
    }

    private removeClassUnitGroupFromClassUnitList(classUnitGroupId) {
        this.classUnits = this.classUnits.filter((classUnit) => classUnit.classUnitGroupId == null
            || classUnit.classUnitGroupId !== classUnitGroupId);
    }

    private removeClassUnitFromClassUnitList(classUnitId) {
        this.classUnits = this.classUnits.filter((classUnit) => classUnit.id !== classUnitId);
    }

    onCreateBookingSuccess() {
        this.loadUserClassUnits();
        TimetableUtil.refetchEventSources(this.calendar);
    }

    onCreateBookingError(error) {
        this.jhiAlertService.error(error.message, null, null);
        this.loadUserClassUnits();
        TimetableUtil.refetchEventSources(this.calendar);
    }

    getCalendarEventSources() {
        return [
            this.timetableHelper.getBookingsSource(
                () => this.user.id, () => this.timetableView.semesterHalf, () => this.timetableView.weekType
            ),
            this.timetableHelper.getRoomsBgEventSource(
                () => this.user.id, () => this.timetableView.semesterHalf, () => this.timetableView.weekType
            ),
            this.timetableHelper.getAcademicUnitsBgEventSource(
                () => this.user.id, () => this.timetableView.semesterHalf, () => this.timetableView.weekType
            ),
            this.timetableHelper.getUsersBgEventSource(
                () => this.user.id, () => this.timetableView.semesterHalf, () => this.timetableView.weekType
            ),
        ];
    }

    updateBooking(event, revertFunc) {
        const booking = new Booking(event.id, event.start, event.end, event.start.day() - 1, this.timetableView.weekType,
            this.timetableView.semesterHalf, event.roomId, this.user.id);

        this.bookingService.updateBooking(booking).subscribe(
            (res: HttpResponse<Booking>) => TimetableUtil.refetchEventSources(this.calendar),
            (res: HttpErrorResponse) => {
                this.jhiAlertService.error(res.error.message, null, null);
                revertFunc();
            }
        );
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

    trackClassUnitById(index: number, item: ClassUnit) {
        return item.id;
    }

    initializeCancelEventConfirmationModal() {
        $('#cancelEventConfirmationModal').on('show.bs.modal',  (event) => {
            TimetableUtil.hideAllEventsPopovers();
            this.selectedEventId = (<any>$(event.relatedTarget)).data('event-id');
        });
    }

    removeBooking() {
        this.bookingService.removeBooking(this.selectedEventId, this.user.id).subscribe(
            (res: HttpResponse<Booking>) => this.onRemoveBookingSuccess(),
            (res: HttpErrorResponse) => this.onRemoveBookingError(res.error)
        );
    }

    onRemoveBookingSuccess() {
        TimetableUtil.hideCancelEventConfirmationModal();
        TimetableUtil.refetchEventSources(this.calendar);
        this.selectedEventId = null;
        this.loadUserClassUnits();
    }

    onRemoveBookingError(error) {
        TimetableUtil.hideCancelEventConfirmationModal();
        this.jhiAlertService.error(error.message, null, null);
    }

    initializeLockBookingBtn() {
        const body = $('body');
        body.off('click', '.lockEventBtn');
        body.on('click', '.lockEventBtn', (event) => {
            event.preventDefault();
            const targetElement = <any>$(event.target);
            const eventId = targetElement.data('event-id');
            const locked = targetElement.data('locked') !== true;

            this.updateBookingLockProperty(eventId, locked);
        });
    }

    updateBookingLockProperty(eventId, locked) {
        const bookingLock = new BookingLock(eventId, this.user.id, locked);

        this.bookingService.lockBooking(bookingLock).subscribe(
            (res: HttpResponse<Booking>) => TimetableUtil.refetchEventSources(this.calendar),
            (res: HttpErrorResponse) => this.jhiAlertService.error(res.error.message, null, null)
        );
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    getAcademicUnitTitle(classUnit) {
        return classUnit.academicUnitGroup ? '-' + AcademicUnitGroupUtil.convertToShortName(classUnit.academicUnitGroup.toString()) : '';
    }
}
