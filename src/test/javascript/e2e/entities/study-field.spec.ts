import { browser, element, by } from 'protractor';
import { NavBarPage } from './../page-objects/jhi-page-objects';

describe('StudyField e2e test', () => {

    let navBarPage: NavBarPage;
    let studyFieldDialogPage: StudyFieldDialogPage;
    let studyFieldComponentsPage: StudyFieldComponentsPage;

    beforeAll(() => {
        browser.get('/');
        browser.waitForAngular();
        navBarPage = new NavBarPage();
        navBarPage.getSignInPage().autoSignInUsing('admin', 'admin');
        browser.waitForAngular();
    });

    it('should load StudyFields', () => {
        navBarPage.goToEntity('study-field');
        studyFieldComponentsPage = new StudyFieldComponentsPage();
        expect(studyFieldComponentsPage.getTitle())
            .toMatch(/geoTimeApp.studyField.home.title/);

    });

    it('should load create StudyField dialog', () => {
        studyFieldComponentsPage.clickOnCreateButton();
        studyFieldDialogPage = new StudyFieldDialogPage();
        expect(studyFieldDialogPage.getModalTitle())
            .toMatch(/geoTimeApp.studyField.home.createOrEditLabel/);
        studyFieldDialogPage.close();
    });

   /* it('should create and save StudyFields', () => {
        studyFieldComponentsPage.clickOnCreateButton();
        studyFieldDialogPage.setNameInput('name');
        expect(studyFieldDialogPage.getNameInput()).toMatch('name');
        studyFieldDialogPage.setShortNameInput('shortName');
        expect(studyFieldDialogPage.getShortNameInput()).toMatch('shortName');
        studyFieldDialogPage.departmentSelectLastOption();
        studyFieldDialogPage.save();
        expect(studyFieldDialogPage.getSaveButton().isPresent()).toBeFalsy();
    });*/

    afterAll(() => {
        navBarPage.autoSignOut();
    });
});

export class StudyFieldComponentsPage {
    createButton = element(by.css('.jh-create-entity'));
    title = element.all(by.css('jhi-study-field div h2 span')).first();

    clickOnCreateButton() {
        return this.createButton.click();
    }

    getTitle() {
        return this.title.getAttribute('jhiTranslate');
    }
}

export class StudyFieldDialogPage {
    modalTitle = element(by.css('h4#myStudyFieldLabel'));
    saveButton = element(by.css('.modal-footer .btn.btn-primary'));
    closeButton = element(by.css('button.close'));
    nameInput = element(by.css('input#field_name'));
    shortNameInput = element(by.css('input#field_shortName'));
    departmentSelect = element(by.css('select#field_department'));

    getModalTitle() {
        return this.modalTitle.getAttribute('jhiTranslate');
    }

    setNameInput = function(name) {
        this.nameInput.sendKeys(name);
    };

    getNameInput = function() {
        return this.nameInput.getAttribute('value');
    };

    setShortNameInput = function(shortName) {
        this.shortNameInput.sendKeys(shortName);
    };

    getShortNameInput = function() {
        return this.shortNameInput.getAttribute('value');
    };

    departmentSelectLastOption = function() {
        this.departmentSelect.all(by.tagName('option')).last().click();
    };

    departmentSelectOption = function(option) {
        this.departmentSelect.sendKeys(option);
    };

    getDepartmentSelect = function() {
        return this.departmentSelect;
    };

    getDepartmentSelectedOption = function() {
        return this.departmentSelect.element(by.css('option:checked')).getText();
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
