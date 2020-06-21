import * as $ from 'jquery';
import {ClassFrequency} from '../shared';

export class TimetableUtil {

    static BOOKING_EVENT_SOURCE_ID = 1;
    static ROOMS_BG_EVENT_SOURCE_ID = 2;
    static ACADEMIC_UNITS_BG_EVENT_SOURCE_ID = 3;
    static USERS_BG_EVENT_SOURCE_ID = 4;
    static ROOM_BG_EXTERNAL_EVENT_SOURCE_ID = 5;
    static ACADEMIC_UNIT_BG_EXTERNAL_EVENT_SOURCE_ID = 6;
    static USERS_BG_EXTERNAL_EVENT_SOURCE_ID = 7;

    static hideEventPopover(element) {
        element.popover('hide');
        element.popover('disable');
    }

    static showEventPopover(element) {
        element.popover('enable');
    }

    static showBackgroundEvents(event, timetableView) {
        const classUnitGroup = event.classUnitGroupId ? event.classUnitGroupId : 'empty';

        this.showRoomBgEvents(event.roomId, classUnitGroup, event.frequency, event.onlySemesterHalf, timetableView);
        this.showAcademicUnitBgEvents(event.academicUnitId, event.academicUnitGroup, classUnitGroup, event.frequency,
            event.onlySemesterHalf, timetableView);
        if (event.relatedAcademicUnitGroups) {
            this.showRelatedAcademicUnitGroupsBgEvents(event.relatedAcademicUnitGroups, classUnitGroup, event.frequency,
                event.onlySemesterHalf, timetableView);
        }
        if (event.relatedUserIds) {
            this.showRelatedUsersBgEvents(event.relatedUserIds, classUnitGroup, event.frequency, event.onlySemesterHalf,
                timetableView);
        }
    }

    static showRoomBgEvents(roomId, classUnitGroup, frequency, onlySemesterHalf, timetableView) {
        const roomClass = '.fc-event-r-' + roomId;
        const classUnitGroupClass = '.fc-event-cug-' + classUnitGroup;
        const weekTypeClass = '.fc-event-wt-' + timetableView.weekType;
        const semesterHalfClass = '.fc-event-sh-' + timetableView.semesterHalf;

        if (frequency === ClassFrequency.SINGLE) {
            (<any>$('.fc-bgevent' + roomClass + weekTypeClass + semesterHalfClass))
                .not(classUnitGroupClass)
                .css({display: 'block'});
            return;
        }

        if (frequency === ClassFrequency.EVERY_WEEK) {
            if (onlySemesterHalf) {
                (<any>$('.fc-bgevent' + roomClass + semesterHalfClass))
                    .not(classUnitGroupClass)
                    .css({display: 'block'});
            } else {
                (<any>$('.fc-bgevent' + roomClass))
                    .not(classUnitGroupClass)
                    .css({display: 'block'});
            }
            return;
        }

        if (frequency === ClassFrequency.EVERY_TWO_WEEKS) {
            if (onlySemesterHalf) {
                (<any>$('.fc-bgevent' + roomClass + weekTypeClass + semesterHalfClass))
                    .not(classUnitGroupClass)
                    .css({display: 'block'});
            } else {
                (<any>$('.fc-bgevent' + roomClass + weekTypeClass))
                    .not(classUnitGroupClass)
                    .css({display: 'block'});
            }
        }
    }

    static showAcademicUnitBgEvents(academicUnitId, academicUnitGroup, classUnitGroup, frequency, onlySemesterHalf, timetableView) {
        const academicUnitClass = '.fc-event-au-' + academicUnitId;
        const academicUnitGroupClass = '.fc-event-aug-' + academicUnitGroup;
        const academicUnitGroupNullClass = '.fc-event-aug-null';
        const classUnitGroupClass = '.fc-event-cug-' + classUnitGroup;
        const weekTypeClass = '.fc-event-wt-' + timetableView.weekType;
        const semesterHalfClass = '.fc-event-sh-' + timetableView.semesterHalf;

        if (frequency === ClassFrequency.SINGLE) {
            if (academicUnitGroup) {
                (<any>$('.fc-bgevent' + academicUnitClass + academicUnitGroupNullClass + weekTypeClass + semesterHalfClass))
                    .not(classUnitGroupClass)
                    .css({display: 'block'});
                (<any>$('.fc-bgevent' + academicUnitClass + academicUnitGroupClass + weekTypeClass + semesterHalfClass))
                    .not(classUnitGroupClass)
                    .css({display: 'block'});
            } else {
                (<any>$('.fc-bgevent' + academicUnitClass + weekTypeClass + semesterHalfClass))
                    .not(classUnitGroupClass)
                    .css({display: 'block'});
            }
            return;
        }

        if (frequency === ClassFrequency.EVERY_WEEK) {
            if (onlySemesterHalf) {
                if (academicUnitGroup) {
                    (<any>$('.fc-bgevent' + academicUnitClass + academicUnitGroupNullClass + semesterHalfClass))
                        .not(classUnitGroupClass)
                        .css({display: 'block'});
                    (<any>$('.fc-bgevent' + academicUnitClass + academicUnitGroupClass + semesterHalfClass))
                        .not(classUnitGroupClass)
                        .css({display: 'block'});
                } else {
                    (<any>$('.fc-bgevent' + academicUnitClass + semesterHalfClass))
                        .not(classUnitGroupClass)
                        .css({display: 'block'});
                }
            } else {
                if (academicUnitGroup) {
                    (<any>$('.fc-bgevent' + academicUnitClass + academicUnitGroupNullClass))
                        .not(classUnitGroupClass)
                        .css({display: 'block'});
                    (<any>$('.fc-bgevent' + academicUnitClass + academicUnitGroupClass))
                        .not(classUnitGroupClass)
                        .css({display: 'block'});
                } else {
                    (<any>$('.fc-bgevent' + academicUnitClass))
                        .not(classUnitGroupClass)
                        .css({display: 'block'});
                }
            }
            return;
        }

        if (frequency === ClassFrequency.EVERY_TWO_WEEKS) {
            if (onlySemesterHalf) {
                if (academicUnitGroup) {
                    (<any>$('.fc-bgevent' + academicUnitClass + academicUnitGroupNullClass + weekTypeClass + semesterHalfClass))
                        .not(classUnitGroupClass)
                        .css({display: 'block'});
                    (<any>$('.fc-bgevent' + academicUnitClass + academicUnitGroupClass + weekTypeClass + semesterHalfClass))
                        .not(classUnitGroupClass)
                        .css({display: 'block'});
                } else {
                    (<any>$('.fc-bgevent' + academicUnitClass + weekTypeClass + semesterHalfClass))
                        .not(classUnitGroupClass)
                        .css({display: 'block'});
                }
            } else {
                if (academicUnitGroup) {
                    (<any>$('.fc-bgevent' + academicUnitClass + academicUnitGroupNullClass + weekTypeClass))
                        .not(classUnitGroupClass)
                        .css({display: 'block'});
                    (<any>$('.fc-bgevent' + academicUnitClass + academicUnitGroupClass + weekTypeClass))
                        .not(classUnitGroupClass)
                        .css({display: 'block'});
                } else {
                    (<any>$('.fc-bgevent' + academicUnitClass + weekTypeClass))
                        .not(classUnitGroupClass)
                        .css({display: 'block'});
                }
            }
        }
    }

    static showRelatedAcademicUnitGroupsBgEvents(relatedAcademicUnitGroups, classUnitGroup, frequency, onlySemesterHalf, timetableView) {
        relatedAcademicUnitGroups.forEach((relatedAcademicUnitGroup) => this.showAcademicUnitBgEvents(
                relatedAcademicUnitGroup.academicUnitId, relatedAcademicUnitGroup.name, classUnitGroup, frequency,
                onlySemesterHalf, timetableView
            )
        );
    }

    static showRelatedUsersBgEvents(relatedUserIds, classUnitGroup, frequency, onlySemesterHalf, timetableView) {
        relatedUserIds.forEach((relatedUserId) =>
            this.showUserBgEvents(relatedUserId, classUnitGroup, frequency, onlySemesterHalf, timetableView)
        );
    }

    static showUserBgEvents(userId, classUnitGroup, frequency, onlySemesterHalf, timetableView) {
        const userClass = '.fc-event-u-' + userId;
        const classUnitGroupClass = '.fc-event-cug-' + classUnitGroup;
        const weekTypeClass = '.fc-event-wt-' + timetableView.weekType;
        const semesterHalfClass = '.fc-event-sh-' + timetableView.semesterHalf;

        if (frequency === ClassFrequency.SINGLE) {
            (<any>$('.fc-bgevent' + userClass + weekTypeClass + semesterHalfClass))
                .not(classUnitGroupClass)
                .css({display: 'block'});
            return;
        }

        if (frequency === ClassFrequency.EVERY_WEEK) {
            if (onlySemesterHalf) {
                (<any>$('.fc-bgevent' + userClass + semesterHalfClass))
                    .not(classUnitGroupClass)
                    .css({display: 'block'});
            } else {
                (<any>$('.fc-bgevent' + userClass))
                    .not(classUnitGroupClass)
                    .css({display: 'block'});
            }
            return;
        }

        if (frequency === ClassFrequency.EVERY_TWO_WEEKS) {
            if (onlySemesterHalf) {
                (<any>$('.fc-bgevent' + userClass + weekTypeClass + semesterHalfClass))
                    .not(classUnitGroupClass)
                    .css({display: 'block'});
            } else {
                (<any>$('.fc-bgevent' + userClass + weekTypeClass))
                    .not(classUnitGroupClass)
                    .css({display: 'block'});
            }
        }
    }

    static hideBackgroundEvents() {
        (<any>$('.fc-bgevent')).css({display: 'none'});
    }

    static handleEventStartDragging(event, timetableView) {
        this.hideEventPopover((<any>$('.fc-event')));
        this.showBackgroundEvents(event, timetableView);
    }

    static handleEventStopDragging() {
        this.showEventPopover((<any>$('.fc-event')));
        this.hideBackgroundEvents();
    }

    static isEventOverlapAllowed(stateEvent, movingEvent, timetableView) {
        if (this.isTheSameClassUnitGroup(stateEvent, movingEvent)) {
            return true;
        } else if (this.isDifferentWeek(stateEvent, movingEvent, timetableView)) {
            return true;
        } else if (this.isRoomsBgEventSource(stateEvent)) {
            return this.isDifferentRoom(stateEvent, movingEvent);
        } else if (this.isAcademicUnitBgEventSource(stateEvent)) {
            return this.isDifferentAcademicUnitGroup(stateEvent, movingEvent) && this.isNotRelatedAcademicUnitGroup(stateEvent, movingEvent);
        } else if (this.isUsersBgEventSource(stateEvent)) {
            return this.isDifferentUser(stateEvent, movingEvent);
        }
        return false;
    }

    private static isTheSameClassUnitGroup(stateEvent, movingEvent) {
        return stateEvent.classUnitGroupId && movingEvent.classUnitGroupId
            && stateEvent.classUnitGroupId === movingEvent.classUnitGroupId;
    }

    private static isDifferentWeek(stateEvent, movingEvent, timetableView) {
        return stateEvent.frequency === ClassFrequency.EVERY_TWO_WEEKS && movingEvent.frequency === ClassFrequency.EVERY_TWO_WEEKS
            && stateEvent.weekType !== timetableView.weekType;
    }

    private static isRoomsBgEventSource(stateEvent) {
        return stateEvent.source.id === this.ROOMS_BG_EVENT_SOURCE_ID.toString()
            || stateEvent.source.id === this.ROOM_BG_EXTERNAL_EVENT_SOURCE_ID.toString();
    }

    private static isDifferentRoom(stateEvent, movingEvent) {
        return stateEvent.roomId.toString() !== movingEvent.roomId.toString();
    }

    private static isAcademicUnitBgEventSource(stateEvent) {
        return stateEvent.source.id === this.ACADEMIC_UNITS_BG_EVENT_SOURCE_ID.toString()
            || stateEvent.source.id === this.ACADEMIC_UNIT_BG_EXTERNAL_EVENT_SOURCE_ID.toString();
    }

    private static isDifferentAcademicUnitGroup(stateEvent, movingEvent) {
        return (stateEvent.academicUnitId !== movingEvent.academicUnitId) || (
            stateEvent.academicUnitGroup && movingEvent.academicUnitGroup && (stateEvent.academicUnitGroup !== movingEvent.academicUnitGroup)
        );
    }

    private static isNotRelatedAcademicUnitGroup(stateEvent, movingEvent) {
        return !movingEvent.relatedAcademicUnitGroups || !movingEvent.relatedAcademicUnitGroups.find(
            (relatedAcademicUnitGroup) => (relatedAcademicUnitGroup.academicUnitId === stateEvent.academicUnitId)
                && (!relatedAcademicUnitGroup.name || relatedAcademicUnitGroup.name === stateEvent.academicUnitGroup)
        );
    }

    private static isUsersBgEventSource(stateEvent) {
        return stateEvent.source.id === this.USERS_BG_EVENT_SOURCE_ID.toString()
            || stateEvent.source.id === this.USERS_BG_EXTERNAL_EVENT_SOURCE_ID.toString();
    }

    private static isDifferentUser(stateEvent, movingEvent) {
        return (stateEvent.userId !== movingEvent.lecturerId)
            && (!movingEvent.relatedUserIds || !movingEvent.relatedUserIds.includes(stateEvent.userId));
    }

    static handleEventDestroy(event, element) {
        this.hideEventPopover(element);
    }

    static refetchEventSources(calendar) {
        calendar.fullCalendar('refetchEvents');
    }

    static allowModifying(calendar) {
        calendar.fullCalendar('option', 'editable', true);
        calendar.fullCalendar('option', 'droppable', true);
    }

    static handleAfterAllRender() {
        $('body').on('click', (e) => {
            if (!$(e.target).parents().hasClass('fc-event') && !$(e.target).parents().hasClass('popover')) {
                (<any>$('.popover')).popover('hide');
            }
        });
    }

    static setDefaultRoom(classUnit) {
        if (classUnit.rooms != null && classUnit.rooms.length > 0) {
            classUnit.selectedRoomId = classUnit.rooms[0].id;
        }
    }

    static hideAllEventsPopovers() {
        (<any>$('.fc-event')).popover('hide');
    }

    static hideCancelEventConfirmationModal() {
        (<any>$('#cancelEventConfirmationModal')).modal('hide');
    }
}
