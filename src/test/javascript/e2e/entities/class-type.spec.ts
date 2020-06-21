import { browser, element, by } from 'protractor';
import { NavBarPage } from './../page-objects/jhi-page-objects';

describe('ClassType e2e test', () => {

    let navBarPage: NavBarPage;
    let classTypeDialogPage: ClassTypeDialogPage;
    let classTypeComponentsPage: ClassTypeComponentsPage;

    beforeAll(() => {
        browser.get('/');
        browser.waitForAngular();
        navBarPage = new NavBarPage();
        navBarPage.getSignInPage().autoSignInUsing('admin', 'admin');
        browser.waitForAngular();
    });

    it('should load ClassTypes', () => {
        navBarPage.goToEntity('class-type');
        classTypeComponentsPage = new ClassTypeComponentsPage();
        expect(classTypeComponentsPage.getTitle())
            .toMatch(/geoTimeApp.classType.home.title/);

    });

    it('should load create ClassType dialog', () => {
        classTypeComponentsPage.clickOnCreateButton();
        classTypeDialogPage = new ClassTypeDialogPage();
        expect(classTypeDialogPage.getModalTitle())
            .toMatch(/geoTimeApp.classType.home.createOrEditLabel/);
        classTypeDialogPage.close();
    });

    it('should create and save ClassTypes', () => {
        classTypeComponentsPage.clickOnCreateButton();
        classTypeDialogPage.setNameInput('name');
        expect(classTypeDialogPage.getNameInput()).toMatch('name');
        classTypeDialogPage.setShortNameInput('shortName');
        expect(classTypeDialogPage.getShortNameInput()).toMatch('shortName');
        classTypeDialogPage.setDescriptionInput('description');
        expect(classTypeDialogPage.getDescriptionInput()).toMatch('description');
        classTypeDialogPage.setColorInput('color');
        expect(classTypeDialogPage.getColorInput()).toMatch('color');
        classTypeDialogPage.save();
        expect(classTypeDialogPage.getSaveButton().isPresent()).toBeFalsy();
    });

    afterAll(() => {
        navBarPage.autoSignOut();
    });
});

export class ClassTypeComponentsPage {
    createButton = element(by.css('.jh-create-entity'));
    title = element.all(by.css('jhi-class-type div h2 span')).first();

    clickOnCreateButton() {
        return this.createButton.click();
    }

    getTitle() {
        return this.title.getAttribute('jhiTranslate');
    }
}

export class ClassTypeDialogPage {
    modalTitle = element(by.css('h4#myClassTypeLabel'));
    saveButton = element(by.css('.modal-footer .btn.btn-primary'));
    closeButton = element(by.css('button.close'));
    nameInput = element(by.css('input#field_name'));
    shortNameInput = element(by.css('input#field_shortName'));
    descriptionInput = element(by.css('input#field_description'));
    colorInput = element(by.css('input#field_color'));

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

    setDescriptionInput = function(description) {
        this.descriptionInput.sendKeys(description);
    };

    getDescriptionInput = function() {
        return this.descriptionInput.getAttribute('value');
    };

    setColorInput = function(color) {
        this.colorInput.sendKeys(color);
    };

    getColorInput = function() {
        return this.colorInput.getAttribute('value');
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
