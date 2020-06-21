import { browser, element, by } from 'protractor';
import { NavBarPage } from './../page-objects/jhi-page-objects';

describe('SchedulingTimeFrame e2e test', () => {

    let navBarPage: NavBarPage;
    let schedulingTimeFrameDialogPage: SchedulingTimeFrameDialogPage;
    let schedulingTimeFrameComponentsPage: SchedulingTimeFrameComponentsPage;

    beforeAll(() => {
        browser.get('/');
        browser.waitForAngular();
        navBarPage = new NavBarPage();
        navBarPage.getSignInPage().autoSignInUsing('admin', 'admin');
        browser.waitForAngular();
    });

    it('should load SchedulingTimeFrames', () => {
        navBarPage.goToEntity('scheduling-time-frame');
        schedulingTimeFrameComponentsPage = new SchedulingTimeFrameComponentsPage();
        expect(schedulingTimeFrameComponentsPage.getTitle())
            .toMatch(/geoTimeApp.schedulingTimeFrame.home.title/);

    });

    it('should load create SchedulingTimeFrame dialog', () => {
        schedulingTimeFrameComponentsPage.clickOnCreateButton();
        schedulingTimeFrameDialogPage = new SchedulingTimeFrameDialogPage();
        expect(schedulingTimeFrameDialogPage.getModalTitle())
            .toMatch(/geoTimeApp.schedulingTimeFrame.home.createOrEditLabel/);
        schedulingTimeFrameDialogPage.close();
    });

   /* it('should create and save SchedulingTimeFrames', () => {
        schedulingTimeFrameComponentsPage.clickOnCreateButton();
        schedulingTimeFrameDialogPage.setStartTimeInput(12310020012301);
        expect(schedulingTimeFrameDialogPage.getStartTimeInput()).toMatch('2001-12-31T02:30');
        schedulingTimeFrameDialogPage.setEndTimeInput(12310020012301);
        expect(schedulingTimeFrameDialogPage.getEndTimeInput()).toMatch('2001-12-31T02:30');
        schedulingTimeFrameDialogPage.userGroupSelectLastOption();
        schedulingTimeFrameDialogPage.subdepartmentSelectLastOption();
        schedulingTimeFrameDialogPage.semesterSelectLastOption();
        schedulingTimeFrameDialogPage.save();
        expect(schedulingTimeFrameDialogPage.getSaveButton().isPresent()).toBeFalsy();
    });*/

    afterAll(() => {
        navBarPage.autoSignOut();
    });
});

export class SchedulingTimeFrameComponentsPage {
    createButton = element(by.css('.jh-create-entity'));
    title = element.all(by.css('jhi-scheduling-time-frame div h2 span')).first();

    clickOnCreateButton() {
        return this.createButton.click();
    }

    getTitle() {
        return this.title.getAttribute('jhiTranslate');
    }
}

export class SchedulingTimeFrameDialogPage {
    modalTitle = element(by.css('h4#mySchedulingTimeFrameLabel'));
    saveButton = element(by.css('.modal-footer .btn.btn-primary'));
    closeButton = element(by.css('button.close'));
    startTimeInput = element(by.css('input#field_startTime'));
    endTimeInput = element(by.css('input#field_endTime'));
    userGroupSelect = element(by.css('select#field_userGroup'));
    subdepartmentSelect = element(by.css('select#field_subdepartment'));
    semesterSelect = element(by.css('select#field_semester'));

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

    userGroupSelectLastOption = function() {
        this.userGroupSelect.all(by.tagName('option')).last().click();
    };

    userGroupSelectOption = function(option) {
        this.userGroupSelect.sendKeys(option);
    };

    getUserGroupSelect = function() {
        return this.userGroupSelect;
    };

    getUserGroupSelectedOption = function() {
        return this.userGroupSelect.element(by.css('option:checked')).getText();
    };

    subdepartmentSelectLastOption = function() {
        this.subdepartmentSelect.all(by.tagName('option')).last().click();
    };

    subdepartmentSelectOption = function(option) {
        this.subdepartmentSelect.sendKeys(option);
    };

    getSubdepartmentSelect = function() {
        return this.subdepartmentSelect;
    };

    getSubdepartmentSelectedOption = function() {
        return this.subdepartmentSelect.element(by.css('option:checked')).getText();
    };

    semesterSelectLastOption = function() {
        this.semesterSelect.all(by.tagName('option')).last().click();
    };

    semesterSelectOption = function(option) {
        this.semesterSelect.sendKeys(option);
    };

    getSemesterSelect = function() {
        return this.semesterSelect;
    };

    getSemesterSelectedOption = function() {
        return this.semesterSelect.element(by.css('option:checked')).getText();
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
