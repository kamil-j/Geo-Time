import { browser, element, by } from 'protractor';
import { NavBarPage } from './../page-objects/jhi-page-objects';

describe('Semester e2e test', () => {

    let navBarPage: NavBarPage;
    let semesterDialogPage: SemesterDialogPage;
    let semesterComponentsPage: SemesterComponentsPage;

    beforeAll(() => {
        browser.get('/');
        browser.waitForAngular();
        navBarPage = new NavBarPage();
        navBarPage.getSignInPage().autoSignInUsing('admin', 'admin');
        browser.waitForAngular();
    });

    it('should load Semesters', () => {
        navBarPage.goToEntity('semester');
        semesterComponentsPage = new SemesterComponentsPage();
        expect(semesterComponentsPage.getTitle())
            .toMatch(/geoTimeApp.semester.home.title/);

    });

    it('should load create Semester dialog', () => {
        semesterComponentsPage.clickOnCreateButton();
        semesterDialogPage = new SemesterDialogPage();
        expect(semesterDialogPage.getModalTitle())
            .toMatch(/geoTimeApp.semester.home.createOrEditLabel/);
        semesterDialogPage.close();
    });

    it('should create and save Semesters', () => {
        semesterComponentsPage.clickOnCreateButton();
        semesterDialogPage.setNameInput('name');
        expect(semesterDialogPage.getNameInput()).toMatch('name');
        semesterDialogPage.setStartDateInput(12310020012301);
        expect(semesterDialogPage.getStartDateInput()).toMatch('2001-12-31T02:30');
        semesterDialogPage.setEndDateInput(12310020012301);
        expect(semesterDialogPage.getEndDateInput()).toMatch('2001-12-31T02:30');
        semesterDialogPage.getActiveInput().isSelected().then((selected) => {
            if (selected) {
                semesterDialogPage.getActiveInput().click();
                expect(semesterDialogPage.getActiveInput().isSelected()).toBeFalsy();
            } else {
                semesterDialogPage.getActiveInput().click();
                expect(semesterDialogPage.getActiveInput().isSelected()).toBeTruthy();
            }
        });
        semesterDialogPage.save();
        expect(semesterDialogPage.getSaveButton().isPresent()).toBeFalsy();
    });

    afterAll(() => {
        navBarPage.autoSignOut();
    });
});

export class SemesterComponentsPage {
    createButton = element(by.css('.jh-create-entity'));
    title = element.all(by.css('jhi-semester div h2 span')).first();

    clickOnCreateButton() {
        return this.createButton.click();
    }

    getTitle() {
        return this.title.getAttribute('jhiTranslate');
    }
}

export class SemesterDialogPage {
    modalTitle = element(by.css('h4#mySemesterLabel'));
    saveButton = element(by.css('.modal-footer .btn.btn-primary'));
    closeButton = element(by.css('button.close'));
    nameInput = element(by.css('input#field_name'));
    startDateInput = element(by.css('input#field_startDate'));
    endDateInput = element(by.css('input#field_endDate'));
    activeInput = element(by.css('input#field_active'));

    getModalTitle() {
        return this.modalTitle.getAttribute('jhiTranslate');
    }

    setNameInput = function(name) {
        this.nameInput.sendKeys(name);
    };

    getNameInput = function() {
        return this.nameInput.getAttribute('value');
    };

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

    getActiveInput = function() {
        return this.activeInput;
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
