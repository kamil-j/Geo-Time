import { browser, element, by } from 'protractor';
import { NavBarPage } from './../page-objects/jhi-page-objects';

describe('BookingUnit e2e test', () => {

    let navBarPage: NavBarPage;
    let bookingUnitDialogPage: BookingUnitDialogPage;
    let bookingUnitComponentsPage: BookingUnitComponentsPage;

    beforeAll(() => {
        browser.get('/');
        browser.waitForAngular();
        navBarPage = new NavBarPage();
        navBarPage.getSignInPage().autoSignInUsing('admin', 'admin');
        browser.waitForAngular();
    });

    it('should load BookingUnits', () => {
        navBarPage.goToEntity('booking-unit');
        bookingUnitComponentsPage = new BookingUnitComponentsPage();
        expect(bookingUnitComponentsPage.getTitle())
            .toMatch(/geoTimeApp.bookingUnit.home.title/);

    });

    it('should load create BookingUnit dialog', () => {
        bookingUnitComponentsPage.clickOnCreateButton();
        bookingUnitDialogPage = new BookingUnitDialogPage();
        expect(bookingUnitDialogPage.getModalTitle())
            .toMatch(/geoTimeApp.bookingUnit.home.createOrEditLabel/);
        bookingUnitDialogPage.close();
    });

   /* it('should create and save BookingUnits', () => {
        bookingUnitComponentsPage.clickOnCreateButton();
        bookingUnitDialogPage.setStartTimeInput(12310020012301);
        expect(bookingUnitDialogPage.getStartTimeInput()).toMatch('2001-12-31T02:30');
        bookingUnitDialogPage.setEndTimeInput(12310020012301);
        expect(bookingUnitDialogPage.getEndTimeInput()).toMatch('2001-12-31T02:30');
        bookingUnitDialogPage.daySelectLastOption();
        bookingUnitDialogPage.weekSelectLastOption();
        bookingUnitDialogPage.semesterHalfSelectLastOption();
        bookingUnitDialogPage.getLockedInput().isSelected().then((selected) => {
            if (selected) {
                bookingUnitDialogPage.getLockedInput().click();
                expect(bookingUnitDialogPage.getLockedInput().isSelected()).toBeFalsy();
            } else {
                bookingUnitDialogPage.getLockedInput().click();
                expect(bookingUnitDialogPage.getLockedInput().isSelected()).toBeTruthy();
            }
        });
        bookingUnitDialogPage.classUnitSelectLastOption();
        bookingUnitDialogPage.roomSelectLastOption();
        bookingUnitDialogPage.save();
        expect(bookingUnitDialogPage.getSaveButton().isPresent()).toBeFalsy();
    });*/

    afterAll(() => {
        navBarPage.autoSignOut();
    });
});

export class BookingUnitComponentsPage {
    createButton = element(by.css('.jh-create-entity'));
    title = element.all(by.css('jhi-booking-unit div h2 span')).first();

    clickOnCreateButton() {
        return this.createButton.click();
    }

    getTitle() {
        return this.title.getAttribute('jhiTranslate');
    }
}

export class BookingUnitDialogPage {
    modalTitle = element(by.css('h4#myBookingUnitLabel'));
    saveButton = element(by.css('.modal-footer .btn.btn-primary'));
    closeButton = element(by.css('button.close'));
    startTimeInput = element(by.css('input#field_startTime'));
    endTimeInput = element(by.css('input#field_endTime'));
    daySelect = element(by.css('select#field_day'));
    weekSelect = element(by.css('select#field_week'));
    semesterHalfSelect = element(by.css('select#field_semesterHalf'));
    lockedInput = element(by.css('input#field_locked'));
    classUnitSelect = element(by.css('select#field_classUnit'));
    roomSelect = element(by.css('select#field_room'));

    getModalTitle() {
        return this.modalTitle.getAttribute('jhiTranslate');
    }

    setStartTimeInput = function(startTime) {
        this.startTimeInput.sendKeys(startTime);
    };

    getStartTimeInput = function() {
        return this.startTimeInput.getAttribute('value');
    };

    setEndTimeInput = function(endTime) {
        this.endTimeInput.sendKeys(endTime);
    };

    getEndTimeInput = function() {
        return this.endTimeInput.getAttribute('value');
    };

    setDaySelect = function(day) {
        this.daySelect.sendKeys(day);
    };

    getDaySelect = function() {
        return this.daySelect.element(by.css('option:checked')).getText();
    };

    daySelectLastOption = function() {
        this.daySelect.all(by.tagName('option')).last().click();
    };
    setWeekSelect = function(week) {
        this.weekSelect.sendKeys(week);
    };

    getWeekSelect = function() {
        return this.weekSelect.element(by.css('option:checked')).getText();
    };

    weekSelectLastOption = function() {
        this.weekSelect.all(by.tagName('option')).last().click();
    };
    setSemesterHalfSelect = function(semesterHalf) {
        this.semesterHalfSelect.sendKeys(semesterHalf);
    };

    getSemesterHalfSelect = function() {
        return this.semesterHalfSelect.element(by.css('option:checked')).getText();
    };

    semesterHalfSelectLastOption = function() {
        this.semesterHalfSelect.all(by.tagName('option')).last().click();
    };
    getLockedInput = function() {
        return this.lockedInput;
    };
    classUnitSelectLastOption = function() {
        this.classUnitSelect.all(by.tagName('option')).last().click();
    };

    classUnitSelectOption = function(option) {
        this.classUnitSelect.sendKeys(option);
    };

    getClassUnitSelect = function() {
        return this.classUnitSelect;
    };

    getClassUnitSelectedOption = function() {
        return this.classUnitSelect.element(by.css('option:checked')).getText();
    };

    roomSelectLastOption = function() {
        this.roomSelect.all(by.tagName('option')).last().click();
    };

    roomSelectOption = function(option) {
        this.roomSelect.sendKeys(option);
    };

    getRoomSelect = function() {
        return this.roomSelect;
    };

    getRoomSelectedOption = function() {
        return this.roomSelect.element(by.css('option:checked')).getText();
    };

    save() {
        this.saveButton.click();
    }

    close() {
        this.closeButton.click();
    }

    getSaveButton() {
        return this.saveButton;
    }
}
