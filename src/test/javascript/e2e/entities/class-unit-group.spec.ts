import { browser, element, by } from 'protractor';
import { NavBarPage } from './../page-objects/jhi-page-objects';

describe('ClassUnitGroup e2e test', () => {

    let navBarPage: NavBarPage;
    let classUnitGroupDialogPage: ClassUnitGroupDialogPage;
    let classUnitGroupComponentsPage: ClassUnitGroupComponentsPage;

    beforeAll(() => {
        browser.get('/');
        browser.waitForAngular();
        navBarPage = new NavBarPage();
        navBarPage.getSignInPage().autoSignInUsing('admin', 'admin');
        browser.waitForAngular();
    });

    it('should load ClassUnitGroups', () => {
        navBarPage.goToEntity('class-unit-group');
        classUnitGroupComponentsPage = new ClassUnitGroupComponentsPage();
        expect(classUnitGroupComponentsPage.getTitle())
            .toMatch(/geoTimeApp.classUnitGroup.home.title/);

    });

    it('should load create ClassUnitGroup dialog', () => {
        classUnitGroupComponentsPage.clickOnCreateButton();
        classUnitGroupDialogPage = new ClassUnitGroupDialogPage();
        expect(classUnitGroupDialogPage.getModalTitle())
            .toMatch(/geoTimeApp.classUnitGroup.home.createOrEditLabel/);
        classUnitGroupDialogPage.close();
    });

   /* it('should create and save ClassUnitGroups', () => {
        classUnitGroupComponentsPage.clickOnCreateButton();
        classUnitGroupDialogPage.setNameInput('name');
        expect(classUnitGroupDialogPage.getNameInput()).toMatch('name');
        classUnitGroupDialogPage.setDescriptionInput('description');
        expect(classUnitGroupDialogPage.getDescriptionInput()).toMatch('description');
        classUnitGroupDialogPage.departmentSelectLastOption();
        classUnitGroupDialogPage.save();
        expect(classUnitGroupDialogPage.getSaveButton().isPresent()).toBeFalsy();
    });*/

    afterAll(() => {
        navBarPage.autoSignOut();
    });
});

export class ClassUnitGroupComponentsPage {
    createButton = element(by.css('.jh-create-entity'));
    title = element.all(by.css('jhi-class-unit-group div h2 span')).first();

    clickOnCreateButton() {
        return this.createButton.click();
    }

    getTitle() {
        return this.title.getAttribute('jhiTranslate');
    }
}

export class ClassUnitGroupDialogPage {
    modalTitle = element(by.css('h4#myClassUnitGroupLabel'));
    saveButton = element(by.css('.modal-footer .btn.btn-primary'));
    closeButton = element(by.css('button.close'));
    nameInput = element(by.css('input#field_name'));
    descriptionInput = element(by.css('input#field_description'));
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

    setDescriptionInput = function(description) {
        this.descriptionInput.sendKeys(description);
    };

    getDescriptionInput = function() {
        return this.descriptionInput.getAttribute('value');
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
