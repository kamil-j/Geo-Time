export class TimetableConf {
    public static DEFAULT = {
        header: {
            left: '',
            center: 'title',
            right: 'customPrev,customNext'
        },
        weekends: false,
        defaultView: 'agendaWeek',
        defaultDate: '1970-01-05',
        allDaySlot: false,
        minTime: '08:00:00',
        maxTime: '22:00:00',
        eventConstraint: {
            start: '08:00:00',
            end: '22:00:00'
        },
        timeFormat: 'H:mm',
        columnFormat: 'dddd',
        slotLabelFormat: 'H:mm',
        height: 'auto',
        firstDay: 1,
        navLinks: false,
        editable: false,
        droppable: false,
        eventDurationEditable: false,
        snapDuration: '00:50:00',
        slotDuration: '00:50',
        eventLimit: true,
        eventColor: '#17a2b8',
        themeSystem: 'bootstrap4',
    };
}
