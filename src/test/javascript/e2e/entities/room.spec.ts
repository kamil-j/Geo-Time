import { browser, element, by } from 'protractor';
import { NavBarPage } from './../page-objects/jhi-page-objects';

describe('Room e2e test', () => {

    let navBarPage: NavBarPage;
    let roomDialogPage: RoomDialogPage;
    let roomComponentsPage: RoomComponentsPage;

    beforeAll(() => {
        browser.get('/');
        browser.waitForAngular();
        navBarPage = new NavBarPage();
        navBarPage.getSignInPage().autoSignInUsing('admin', 'admin');
        browser.waitForAngular();
    });

    it('should load Rooms', () => {
        navBarPage.goToEntity('room');
        roomComponentsPage = new RoomComponentsPage();
        expect(roomComponentsPage.getTitle())
            .toMatch(/geoTimeApp.room.home.title/);

    });

    it('should load create Room dialog', () => {
        roomComponentsPage.clickOnCreateButton();
        roomDialogPage = new RoomDialogPage();
        expect(roomDialogPage.getModalTitle())
            .toMatch(/geoTimeApp.room.home.createOrEditLabel/);
        roomDialogPage.close();
    });

   /* it('should create and save Rooms', () => {
        roomComponentsPage.clickOnCreateButton();
        roomDialogPage.setNameInput('name');
        expect(roomDialogPage.getNameInput()).toMatch('name');
        roomDialogPage.setCapacityInput('5');
        expect(roomDialogPage.getCapacityInput()).toMatch('5');
        roomDialogPage.roomTypeSelectLastOption();
        roomDialogPage.locationSelectLastOption();
        roomDialogPage.save();
        expect(roomDialogPage.getSaveButton().isPresent()).toBeFalsy();
    });*/

    afterAll(() => {
        navBarPage.autoSignOut();
    });
});

export class RoomComponentsPage {
    createButton = element(by.css('.jh-create-entity'));
    title = element.all(by.css('jhi-room div h2 span')).first();

    clickOnCreateButton() {
        return this.createButton.click();
    }

    getTitle() {
        return this.title.getAttribute('jhiTranslate');
    }
}

export class RoomDialogPage {
    modalTitle = element(by.css('h4#myRoomLabel'));
    saveButton = element(by.css('.modal-footer .btn.btn-primary'));
    closeButton = element(by.css('button.close'));
    nameInput = element(by.css('input#field_name'));
    capacityInput = element(by.css('input#field_capacity'));
    roomTypeSelect = element(by.css('select#field_roomType'));
    locationSelect = element(by.css('select#field_location'));

    getModalTitle() {
        return this.modalTitle.getAttribute('jhiTranslate');
    }

    setNameInput = function(name) {
        this.nameInput.sendKeys(name);
    };

    getNameInput = function() {
        return this.nameInput.getAttribute('value');
    };

    setCapacityInput = function(capacity) {
        this.capacityInput.sendKeys(capacity);
    };

    getCapacityInput = function() {
        return this.capacityInput.getAttribute('value');
    };

    roomTypeSelectLastOption = function() {
        this.roomTypeSelect.all(by.tagName('option')).last().click();
    };

    roomTypeSelectOption = function(option) {
        this.roomTypeSelect.sendKeys(option);
    };

    getRoomTypeSelect = function() {
        return this.roomTypeSelect;
    };

    getRoomTypeSelectedOption = function() {
        return this.roomTypeSelect.element(by.css('option:checked')).getText();
    };

    locationSelectLastOption = function() {
        this.locationSelect.all(by.tagName('option')).last().click();
    };

    locationSelectOption = function(option) {
        this.locationSelect.sendKeys(option);
    };

    getLocationSelect = function() {
        return this.locationSelect;
    };

    getLocationSelectedOption = function() {
        return this.locationSelect.element(by.css('option:checked')).getText();
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
