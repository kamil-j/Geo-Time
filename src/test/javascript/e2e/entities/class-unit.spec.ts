import { browser, element, by } from 'protractor';
import { NavBarPage } from './../page-objects/jhi-page-objects';

describe('ClassUnit e2e test', () => {

    let navBarPage: NavBarPage;
    let classUnitDialogPage: ClassUnitDialogPage;
    let classUnitComponentsPage: ClassUnitComponentsPage;

    beforeAll(() => {
        browser.get('/');
        browser.waitForAngular();
        navBarPage = new NavBarPage();
        navBarPage.getSignInPage().autoSignInUsing('admin', 'admin');
        browser.waitForAngular();
    });

    it('should load ClassUnits', () => {
        navBarPage.goToEntity('class-unit');
        classUnitComponentsPage = new ClassUnitComponentsPage();
        expect(classUnitComponentsPage.getTitle())
            .toMatch(/geoTimeApp.classUnit.home.title/);

    });

    it('should load create ClassUnit dialog', () => {
        classUnitComponentsPage.clickOnCreateButton();
        classUnitDialogPage = new ClassUnitDialogPage();
        expect(classUnitDialogPage.getModalTitle())
            .toMatch(/geoTimeApp.classUnit.home.createOrEditLabel/);
        classUnitDialogPage.close();
    });

   /* it('should create and save ClassUnits', () => {
        classUnitComponentsPage.clickOnCreateButton();
        classUnitDialogPage.setTitleInput('title');
        expect(classUnitDialogPage.getTitleInput()).toMatch('title');
        classUnitDialogPage.setDescriptionInput('description');
        expect(classUnitDialogPage.getDescriptionInput()).toMatch('description');
        classUnitDialogPage.setDurationInput('5');
        expect(classUnitDialogPage.getDurationInput()).toMatch('5');
        classUnitDialogPage.setHoursQuantityInput('5');
        expect(classUnitDialogPage.getHoursQuantityInput()).toMatch('5');
        classUnitDialogPage.frequencySelectLastOption();
        classUnitDialogPage.academicUnitGroupSelectLastOption();
        classUnitDialogPage.classTypeSelectLastOption();
        classUnitDialogPage.userExtSelectLastOption();
        // classUnitDialogPage.roomSelectLastOption();
        classUnitDialogPage.semesterSelectLastOption();
        classUnitDialogPage.academicUnitSelectLastOption();
        classUnitDialogPage.classUnitGroupSelectLastOption();
        classUnitDialogPage.subdepartmentSelectLastOption();
        classUnitDialogPage.save();
        expect(classUnitDialogPage.getSaveButton().isPresent()).toBeFalsy();
    });*/

    afterAll(() => {
        navBarPage.autoSignOut();
    });
});

export class ClassUnitComponentsPage {
    createButton = element(by.css('.jh-create-entity'));
    title = element.all(by.css('jhi-class-unit div h2 span')).first();

    clickOnCreateButton() {
        return this.createButton.click();
    }

    getTitle() {
        return this.title.getAttribute('jhiTranslate');
    }
}

export class ClassUnitDialogPage {
    modalTitle = element(by.css('h4#myClassUnitLabel'));
    saveButton = element(by.css('.modal-footer .btn.btn-primary'));
    closeButton = element(by.css('button.close'));
    titleInput = element(by.css('input#field_title'));
    descriptionInput = element(by.css('input#field_description'));
    durationInput = element(by.css('input#field_duration'));
    hoursQuantityInput = element(by.css('input#field_hoursQuantity'));
    frequencySelect = element(by.css('select#field_frequency'));
    academicUnitGroupSelect = element(by.css('select#field_academicUnitGroup'));
    classTypeSelect = element(by.css('select#field_classType'));
    userExtSelect = element(by.css('select#field_userExt'));
    roomSelect = element(by.css('select#field_room'));
    semesterSelect = element(by.css('select#field_semester'));
    academicUnitSelect = element(by.css('select#field_academicUnit'));
    classUnitGroupSelect = element(by.css('select#field_classUnitGroup'));
    subdepartmentSelect = element(by.css('select#field_subdepartment'));

    getModalTitle() {
        return this.modalTitle.getAttribute('jhiTranslate');
    }

    setTitleInput = function(title) {
        this.titleInput.sendKeys(title);
    };

    getTitleInput = function() {
        return this.titleInput.getAttribute('value');
    };

    setDescriptionInput = function(description) {
        this.descriptionInput.sendKeys(description);
    };

    getDescriptionInput = function() {
        return this.descriptionInput.getAttribute('value');
    };

    setDurationInput = function(duration) {
        this.durationInput.sendKeys(duration);
    };

    getDurationInput = function() {
        return this.durationInput.getAttribute('value');
    };

    setHoursQuantityInput = function(hoursQuantity) {
        this.hoursQuantityInput.sendKeys(hoursQuantity);
    };

    getHoursQuantityInput = function() {
        return this.hoursQuantityInput.getAttribute('value');
    };

    setFrequencySelect = function(frequency) {
        this.frequencySelect.sendKeys(frequency);
    };

    getFrequencySelect = function() {
        return this.frequencySelect.element(by.css('option:checked')).getText();
    };

    frequencySelectLastOption = function() {
        this.frequencySelect.all(by.tagName('option')).last().click();
    };
    setAcademicUnitGroupSelect = function(academicUnitGroup) {
        this.academicUnitGroupSelect.sendKeys(academicUnitGroup);
    };

    getAcademicUnitGroupSelect = function() {
        return this.academicUnitGroupSelect.element(by.css('option:checked')).getText();
    };

    academicUnitGroupSelectLastOption = function() {
        this.academicUnitGroupSelect.all(by.tagName('option')).last().click();
    };
    classTypeSelectLastOption = function() {
        this.classTypeSelect.all(by.tagName('option')).last().click();
    };

    classTypeSelectOption = function(option) {
        this.classTypeSelect.sendKeys(option);
    };

    getClassTypeSelect = function() {
        return this.classTypeSelect;
    };

    getClassTypeSelectedOption = function() {
        return this.classTypeSelect.element(by.css('option:checked')).getText();
    };

    userExtSelectLastOption = function() {
        this.userExtSelect.all(by.tagName('option')).last().click();
    };

    userExtSelectOption = function(option) {
        this.userExtSelect.sendKeys(option);
    };

    getUserExtSelect = function() {
        return this.userExtSelect;
    };

    getUserExtSelectedOption = function() {
        return this.userExtSelect.element(by.css('option:checked')).getText();
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

    academicUnitSelectLastOption = function() {
        this.academicUnitSelect.all(by.tagName('option')).last().click();
    };

    academicUnitSelectOption = function(option) {
        this.academicUnitSelect.sendKeys(option);
    };

    getAcademicUnitSelect = function() {
        return this.academicUnitSelect;
    };

    getAcademicUnitSelectedOption = function() {
        return this.academicUnitSelect.element(by.css('option:checked')).getText();
    };

    classUnitGroupSelectLastOption = function() {
        this.classUnitGroupSelect.all(by.tagName('option')).last().click();
    };

    classUnitGroupSelectOption = function(option) {
        this.classUnitGroupSelect.sendKeys(option);
    };

    getClassUnitGroupSelect = function() {
        return this.classUnitGroupSelect;
    };

    getClassUnitGroupSelectedOption = function() {
        return this.classUnitGroupSelect.element(by.css('option:checked')).getText();
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
