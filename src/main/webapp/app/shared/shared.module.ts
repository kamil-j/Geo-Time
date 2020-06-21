import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { DatePipe } from '@angular/common';

import {
    GeoTimeSharedLibsModule,
    GeoTimeSharedCommonModule,
    CSRFService,
    AuthServerProvider,
    AccountService,
    UserService,
    StateStorageService,
    LoginService,
    LoginModalService,
    JhiLoginModalComponent,
    Principal,
    HasAnyAuthorityDirective,
    SemesterInfoService,
    BookingService,
    WizardComponent,
    WizardStepComponent
} from './';
import {DepartmentInfoService} from './department/department-info.service';
import {AcademicUnitInfoService} from './academicUnit/academic-unit-info.service';

@NgModule({
    imports: [
        GeoTimeSharedLibsModule,
        GeoTimeSharedCommonModule
    ],
    declarations: [
        JhiLoginModalComponent,
        WizardComponent,
        WizardStepComponent,
        HasAnyAuthorityDirective,
    ],
    providers: [
        LoginService,
        LoginModalService,
        AccountService,
        StateStorageService,
        Principal,
        CSRFService,
        AuthServerProvider,
        UserService,
        DatePipe,
        DepartmentInfoService,
        AcademicUnitInfoService,
        SemesterInfoService,
        BookingService
    ],
    entryComponents: [JhiLoginModalComponent],
    exports: [
        GeoTimeSharedCommonModule,
        JhiLoginModalComponent,
        WizardComponent,
        WizardStepComponent,
        HasAnyAuthorityDirective,
        DatePipe
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]

})
export class GeoTimeSharedModule {}
