import { browser, element, by } from 'protractor';
import { NavBarPage } from './../page-objects/jhi-page-objects';

describe('Subdepartment e2e test', () => {

    let navBarPage: NavBarPage;
    let subdepartmentDialogPage: SubdepartmentDialogPage;
    let subdepartmentComponentsPage: SubdepartmentComponentsPage;

    beforeAll(() => {
        browser.get('/');
        browser.waitForAngular();
        navBarPage = new NavBarPage();
        navBarPage.getSignInPage().autoSignInUsing('admin', 'admin');
        browser.waitForAngular();
    });

    it('should load Subdepartments', () => {
        navBarPage.goToEntity('subdepartment');
        subdepartmentComponentsPage = new SubdepartmentComponentsPage();
        expect(subdepartmentComponentsPage.getTitle())
            .toMatch(/geoTimeApp.subdepartment.home.title/);

    });

    it('should load create Subdepartment dialog', () => {
        subdepartmentComponentsPage.clickOnCreateButton();
        subdepartmentDialogPage = new SubdepartmentDialogPage();
        expect(subdepartmentDialogPage.getModalTitle())
            .toMatch(/geoTimeApp.subdepartment.home.createOrEditLabel/);
        subdepartmentDialogPage.close();
    });

    it('should create and save Subdepartments', () => {
        subdepartmentComponentsPage.clickOnCreateButton();
        subdepartmentDialogPage.setNameInput('name');
        expect(subdepartmentDialogPage.getNameInput()).toMatch('name');
        subdepartmentDialogPage.setShortNameInput('shortName');
        expect(subdepartmentDialogPage.getShortNameInput()).toMatch('shortName');
        subdepartmentDialogPage.save();
        expect(subdepartmentDialogPage.getSaveButton().isPresent()).toBeFalsy();
    });

    afterAll(() => {
        navBarPage.autoSignOut();
    });
});

export class SubdepartmentComponentsPage {
    createButton = element(by.css('.jh-create-entity'));
    title = element.all(by.css('jhi-subdepartment div h2 span')).first();

    clickOnCreateButton() {
        return this.createButton.click();
    }

    getTitle() {
        return this.title.getAttribute('jhiTranslate');
    }
}

export class SubdepartmentDialogPage {
    modalTitle = element(by.css('h4#mySubdepartmentLabel'));
    saveButton = element(by.css('.modal-footer .btn.btn-primary'));
    closeButton = element(by.css('button.close'));
    nameInput = element(by.css('input#field_name'));
    shortNameInput = element(by.css('input#field_shortName'));

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
