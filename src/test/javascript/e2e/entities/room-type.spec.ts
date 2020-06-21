import { browser, element, by } from 'protractor';
import { NavBarPage } from './../page-objects/jhi-page-objects';

describe('RoomType e2e test', () => {

    let navBarPage: NavBarPage;
    let roomTypeDialogPage: RoomTypeDialogPage;
    let roomTypeComponentsPage: RoomTypeComponentsPage;

    beforeAll(() => {
        browser.get('/');
        browser.waitForAngular();
        navBarPage = new NavBarPage();
        navBarPage.getSignInPage().autoSignInUsing('admin', 'admin');
        browser.waitForAngular();
    });

    it('should load RoomTypes', () => {
        navBarPage.goToEntity('room-type');
        roomTypeComponentsPage = new RoomTypeComponentsPage();
        expect(roomTypeComponentsPage.getTitle())
            .toMatch(/geoTimeApp.roomType.home.title/);

    });

    it('should load create RoomType dialog', () => {
        roomTypeComponentsPage.clickOnCreateButton();
        roomTypeDialogPage = new RoomTypeDialogPage();
        expect(roomTypeDialogPage.getModalTitle())
            .toMatch(/geoTimeApp.roomType.home.createOrEditLabel/);
        roomTypeDialogPage.close();
    });

    it('should create and save RoomTypes', () => {
        roomTypeComponentsPage.clickOnCreateButton();
        roomTypeDialogPage.setNameInput('name');
        expect(roomTypeDialogPage.getNameInput()).toMatch('name');
        roomTypeDialogPage.setDescriptionInput('description');
        expect(roomTypeDialogPage.getDescriptionInput()).toMatch('description');
        roomTypeDialogPage.save();
        expect(roomTypeDialogPage.getSaveButton().isPresent()).toBeFalsy();
    });

    afterAll(() => {
        navBarPage.autoSignOut();
    });
});

export class RoomTypeComponentsPage {
    createButton = element(by.css('.jh-create-entity'));
    title = element.all(by.css('jhi-room-type div h2 span')).first();

    clickOnCreateButton() {
        return this.createButton.click();
    }

    getTitle() {
        return this.title.getAttribute('jhiTranslate');
    }
}

export class RoomTypeDialogPage {
    modalTitle = element(by.css('h4#myRoomTypeLabel'));
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
