import { browser, element, by } from 'protractor';
import { NavBarPage } from './../page-objects/jhi-page-objects';

describe('Department e2e test', () => {

    let navBarPage: NavBarPage;
    let departmentDialogPage: DepartmentDialogPage;
    let departmentComponentsPage: DepartmentComponentsPage;

    beforeAll(() => {
        browser.get('/');
        browser.waitForAngular();
        navBarPage = new NavBarPage();
        navBarPage.getSignInPage().autoSignInUsing('admin', 'admin');
        browser.waitForAngular();
    });

    it('should load Departments', () => {
        navBarPage.goToEntity('department');
        departmentComponentsPage = new DepartmentComponentsPage();
        expect(departmentComponentsPage.getTitle())
            .toMatch(/geoTimeApp.department.home.title/);

    });

    it('should load create Department dialog', () => {
        departmentComponentsPage.clickOnCreateButton();
        departmentDialogPage = new DepartmentDialogPage();
        expect(departmentDialogPage.getModalTitle())
            .toMatch(/geoTimeApp.department.home.createOrEditLabel/);
        departmentDialogPage.close();
    });

    it('should create and save Departments', () => {
        departmentComponentsPage.clickOnCreateButton();
        departmentDialogPage.setNameInput('name');
        expect(departmentDialogPage.getNameInput()).toMatch('name');
        departmentDialogPage.setShortNameInput('shortName');
        expect(departmentDialogPage.getShortNameInput()).toMatch('shortName');
        departmentDialogPage.getFunctionalInput().isSelected().then((selected) => {
            if (selected) {
                departmentDialogPage.getFunctionalInput().click();
                expect(departmentDialogPage.getFunctionalInput().isSelected()).toBeFalsy();
            } else {
                departmentDialogPage.getFunctionalInput().click();
                expect(departmentDialogPage.getFunctionalInput().isSelected()).toBeTruthy();
            }
        });
        departmentDialogPage.save();
        expect(departmentDialogPage.getSaveButton().isPresent()).toBeFalsy();
    });

    afterAll(() => {
        navBarPage.autoSignOut();
    });
});

export class DepartmentComponentsPage {
    createButton = element(by.css('.jh-create-entity'));
    title = element.all(by.css('jhi-department div h2 span')).first();

    clickOnCreateButton() {
        return this.createButton.click();
    }

    getTitle() {
        return this.title.getAttribute('jhiTranslate');
    }
}

export class DepartmentDialogPage {
    modalTitle = element(by.css('h4#myDepartmentLabel'));
    saveButton = element(by.css('.modal-footer .btn.btn-primary'));
    closeButton = element(by.css('button.close'));
    nameInput = element(by.css('input#field_name'));
    shortNameInput = element(by.css('input#field_shortName'));
    functionalInput = element(by.css('input#field_functional'));

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

    getFunctionalInput = function() {
        return this.functionalInput;
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
