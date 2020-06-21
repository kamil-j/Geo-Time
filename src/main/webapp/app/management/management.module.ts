import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { GeoTimeSharedModule } from '../shared';
import {
    managementState,
    UserMgmtComponent,
    UserDialogComponent,
    UserDeleteDialogComponent,
    UserMgmtDetailComponent,
    UserMgmtDialogComponent,
    UserMgmtDeleteDialogComponent,
    UserMgmtResolvePagingParams,
    UserModalService,
    UserMgmtUploadDialogComponent,
    UserUploadDialogComponent,
    DepartmentPlanningMetricsComponent,
    DepartmentPlanningMetricsService,
    SubdepartmentPlanningMetricsComponent,
    SubdepartmentPlanningMetricsService
} from './';

@NgModule({
    imports: [
        GeoTimeSharedModule,
        RouterModule.forChild(managementState),
    ],
    declarations: [
        DepartmentPlanningMetricsComponent,
        SubdepartmentPlanningMetricsComponent,
        UserMgmtComponent,
        UserDialogComponent,
        UserDeleteDialogComponent,
        UserUploadDialogComponent,
        UserMgmtDetailComponent,
        UserMgmtDialogComponent,
        UserMgmtDeleteDialogComponent,
        UserMgmtUploadDialogComponent
    ],
    entryComponents: [
        UserMgmtDialogComponent,
        UserMgmtDeleteDialogComponent,
        UserMgmtUploadDialogComponent
    ],
    providers: [
        DepartmentPlanningMetricsService,
        SubdepartmentPlanningMetricsService,
        UserMgmtResolvePagingParams,
        UserModalService
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class GeoTimeManagementModule {}
