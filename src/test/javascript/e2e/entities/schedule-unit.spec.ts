import { browser, element, by } from 'protractor';
import { NavBarPage } from './../page-objects/jhi-page-objects';

describe('ScheduleUnit e2e test', () => {

    let navBarPage: NavBarPage;
    let scheduleUnitDialogPage: ScheduleUnitDialogPage;
    let scheduleUnitComponentsPage: ScheduleUnitComponentsPage;

    beforeAll(() => {
        browser.get('/');
        browser.waitForAngular();
        navBarPage = new NavBarPage();
        navBarPage.getSignInPage().autoSignInUsing('admin', 'admin');
        browser.waitForAngular();
    });

    it('should load ScheduleUnits', () => {
        navBarPage.goToEntity('schedule-unit');
        scheduleUnitComponentsPage = new ScheduleUnitComponentsPage();
        expect(scheduleUnitComponentsPage.getTitle())
            .toMatch(/geoTimeApp.scheduleUnit.home.title/);

    });

    it('should load create ScheduleUnit dialog', () => {
        scheduleUnitComponentsPage.clickOnCreateButton();
        scheduleUnitDialogPage = new ScheduleUnitDialogPage();
        expect(scheduleUnitDialogPage.getModalTitle())
            .toMatch(/geoTimeApp.scheduleUnit.home.createOrEditLabel/);
        scheduleUnitDialogPage.close();
    });

   /* it('should create and save ScheduleUnits', () => {
        scheduleUnitComponentsPage.clickOnCreateButton();
        scheduleUnitDialogPage.setStartDateInput(12310020012301);
        expect(scheduleUnitDialogPage.getStartDateInput()).toMatch('2001-12-31T02:30');
        scheduleUnitDialogPage.setEndDateInput(12310020012301);
        expect(scheduleUnitDialogPage.getEndDateInput()).toMatch('2001-12-31T02:30');
        scheduleUnitDialogPage.classUnitSelectLastOption();
        scheduleUnitDialogPage.roomSelectLastOption();
        scheduleUnitDialogPage.save();
        expect(scheduleUnitDialogPage.getSaveButton().isPresent()).toBeFalsy();
    });*/

    afterAll(() => {
        navBarPage.autoSignOut();
    });
});

export class ScheduleUnitComponentsPage {
    createButton = element(by.css('.jh-create-entity'));
    title = element.all(by.css('jhi-schedule-unit div h2 span')).first();

    clickOnCreateButton() {
        return this.createButton.click();
    }

    getTitle() {
        return this.title.getAttribute('jhiTranslate');
    }
}

export class ScheduleUnitDialogPage {
    modalTitle = element(by.css('h4#myScheduleUnitLabel'));
    saveButton = element(by.css('.modal-footer .btn.btn-primary'));
    closeButton = element(by.css('button.close'));
    startDateInput = element(by.css('input#field_startDate'));
    endDateInput = element(by.css('input#field_endDate'));
    classUnitSelect = element(by.css('select#field_classUnit'));
    roomSelect = element(by.css('select#field_room'));

    getModalTitle() {
        return this.modalTitle.getAttribute('jhiTranslate');
    }

    setStartDateInput = function(startDate) {
        this.startDateInput.sendKeys(startDate);
    };

    getStartDateInput = function() {
        return this.startDateInput.getAttribute('value');
    };

    setEndDateInput = function(endDate) {
        this.endDateInput.sendKeys(endDate);
    };

    getEndDateInput = function() {
        return this.endDateInput.getAttribute('value');
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
