import { browser, element, by } from 'protractor';
import { NavBarPage } from './../page-objects/jhi-page-objects';

describe('UserGroup e2e test', () => {

    let navBarPage: NavBarPage;
    let userGroupDialogPage: UserGroupDialogPage;
    let userGroupComponentsPage: UserGroupComponentsPage;

    beforeAll(() => {
        browser.get('/');
        browser.waitForAngular();
        navBarPage = new NavBarPage();
        navBarPage.getSignInPage().autoSignInUsing('admin', 'admin');
        browser.waitForAngular();
    });

    it('should load UserGroups', () => {
        navBarPage.goToEntity('user-group');
        userGroupComponentsPage = new UserGroupComponentsPage();
        expect(userGroupComponentsPage.getTitle())
            .toMatch(/geoTimeApp.userGroup.home.title/);

    });

    it('should load create UserGroup dialog', () => {
        userGroupComponentsPage.clickOnCreateButton();
        userGroupDialogPage = new UserGroupDialogPage();
        expect(userGroupDialogPage.getModalTitle())
            .toMatch(/geoTimeApp.userGroup.home.createOrEditLabel/);
        userGroupDialogPage.close();
    });

    it('should create and save UserGroups', () => {
        userGroupComponentsPage.clickOnCreateButton();
        userGroupDialogPage.setNameInput('name');
        expect(userGroupDialogPage.getNameInput()).toMatch('name');
        userGroupDialogPage.setDescriptionInput('description');
        expect(userGroupDialogPage.getDescriptionInput()).toMatch('description');
        userGroupDialogPage.save();
        expect(userGroupDialogPage.getSaveButton().isPresent()).toBeFalsy();
    });

    afterAll(() => {
        navBarPage.autoSignOut();
    });
});

export class UserGroupComponentsPage {
    createButton = element(by.css('.jh-create-entity'));
    title = element.all(by.css('jhi-user-group div h2 span')).first();

    clickOnCreateButton() {
        return this.createButton.click();
    }

    getTitle() {
        return this.title.getAttribute('jhiTranslate');
    }
}

export class UserGroupDialogPage {
    modalTitle = element(by.css('h4#myUserGroupLabel'));
    saveButton = element(by.css('.modal-footer .btn.btn-primary'));
    closeButton = element(by.css('button.close'));
    nameInput = element(by.css('input#field_name'));
    descriptionInput = element(by.css('input#field_description'));

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
