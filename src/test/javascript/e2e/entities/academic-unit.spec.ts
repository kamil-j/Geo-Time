import { browser, element, by } from 'protractor';
import { NavBarPage } from './../page-objects/jhi-page-objects';

describe('AcademicUnit e2e test', () => {

    let navBarPage: NavBarPage;
    let academicUnitDialogPage: AcademicUnitDialogPage;
    let academicUnitComponentsPage: AcademicUnitComponentsPage;

    beforeAll(() => {
        browser.get('/');
        browser.waitForAngular();
        navBarPage = new NavBarPage();
        navBarPage.getSignInPage().autoSignInUsing('admin', 'admin');
        browser.waitForAngular();
    });

    it('should load AcademicUnits', () => {
        navBarPage.goToEntity('academic-unit');
        academicUnitComponentsPage = new AcademicUnitComponentsPage();
        expect(academicUnitComponentsPage.getTitle())
            .toMatch(/geoTimeApp.academicUnit.home.title/);

    });

    it('should load create AcademicUnit dialog', () => {
        academicUnitComponentsPage.clickOnCreateButton();
        academicUnitDialogPage = new AcademicUnitDialogPage();
        expect(academicUnitDialogPage.getModalTitle())
            .toMatch(/geoTimeApp.academicUnit.home.createOrEditLabel/);
        academicUnitDialogPage.close();
    });

   /* it('should create and save AcademicUnits', () => {
        academicUnitComponentsPage.clickOnCreateButton();
        academicUnitDialogPage.setNameInput('name');
        expect(academicUnitDialogPage.getNameInput()).toMatch('name');
        academicUnitDialogPage.yearSelectLastOption();
        academicUnitDialogPage.degreeSelectLastOption();
        academicUnitDialogPage.setDescriptionInput('description');
        expect(academicUnitDialogPage.getDescriptionInput()).toMatch('description');
        academicUnitDialogPage.studyFieldSelectLastOption();
        academicUnitDialogPage.save();
        expect(academicUnitDialogPage.getSaveButton().isPresent()).toBeFalsy();
    });*/

    afterAll(() => {
        navBarPage.autoSignOut();
    });
});

export class AcademicUnitComponentsPage {
    createButton = element(by.css('.jh-create-entity'));
    title = element.all(by.css('jhi-academic-unit div h2 span')).first();

    clickOnCreateButton() {
        return this.createButton.click();
    }

    getTitle() {
        return this.title.getAttribute('jhiTranslate');
    }
}

export class AcademicUnitDialogPage {
    modalTitle = element(by.css('h4#myAcademicUnitLabel'));
    saveButton = element(by.css('.modal-footer .btn.btn-primary'));
    closeButton = element(by.css('button.close'));
    nameInput = element(by.css('input#field_name'));
    yearSelect = element(by.css('select#field_year'));
    degreeSelect = element(by.css('select#field_degree'));
    descriptionInput = element(by.css('input#field_description'));
    studyFieldSelect = element(by.css('select#field_studyField'));

    getModalTitle() {
        return this.modalTitle.getAttribute('jhiTranslate');
    }

    setNameInput = function(name) {
        this.nameInput.sendKeys(name);
    };

    getNameInput = function() {
        return this.nameInput.getAttribute('value');
    };

    setYearSelect = function(year) {
        this.yearSelect.sendKeys(year);
    };

    getYearSelect = function() {
        return this.yearSelect.element(by.css('option:checked')).getText();
    };

    yearSelectLastOption = function() {
        this.yearSelect.all(by.tagName('option')).last().click();
    };
    setDegreeSelect = function(degree) {
        this.degreeSelect.sendKeys(degree);
    };

    getDegreeSelect = function() {
        return this.degreeSelect.element(by.css('option:checked')).getText();
    };

    degreeSelectLastOption = function() {
        this.degreeSelect.all(by.tagName('option')).last().click();
    };
    setDescriptionInput = function(description) {
        this.descriptionInput.sendKeys(description);
    };

    getDescriptionInput = function() {
        return this.descriptionInput.getAttribute('value');
    };

    studyFieldSelectLastOption = function() {
        this.studyFieldSelect.all(by.tagName('option')).last().click();
    };

    studyFieldSelectOption = function(option) {
        this.studyFieldSelect.sendKeys(option);
    };

    getStudyFieldSelect = function() {
        return this.studyFieldSelect;
    };

    getStudyFieldSelectedOption = function() {
        return this.studyFieldSelect.element(by.css('option:checked')).getText();
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
