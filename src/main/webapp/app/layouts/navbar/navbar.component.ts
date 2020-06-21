import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import {JhiAlertService, JhiLanguageService} from 'ng-jhipster';
import { ProfileService } from '../profiles/profile.service';
import { JhiLanguageHelper, Principal, LoginModalService, LoginService, StudyField } from '../../shared';
import { VERSION } from '../../app.constants';
import {HttpErrorResponse, HttpResponse} from '@angular/common/http';
import {DepartmentInfoService} from '../../shared/department/department-info.service';
import {DepartmentInfo} from '../../shared/department/department-info.model';

@Component({
    selector: 'jhi-navbar',
    templateUrl: './navbar.component.html',
    styleUrls: [
        'navbar.css'
    ]
})
export class NavbarComponent implements OnInit {
    inProduction: boolean;
    languages: any[];
    swaggerEnabled: boolean;
    modalRef: NgbModalRef;
    version: string;
    departments: DepartmentInfo[];

    constructor(
        private loginService: LoginService,
        private languageService: JhiLanguageService,
        private languageHelper: JhiLanguageHelper,
        private principal: Principal,
        private loginModalService: LoginModalService,
        private profileService: ProfileService,
        private departmentInfoService: DepartmentInfoService,
        private jhiAlertService: JhiAlertService,
        private router: Router
    ) {
        this.version = VERSION ? 'v' + VERSION : '';
    }

    ngOnInit() {
        this.languageHelper.getAll().then((languages) => {
            this.languages = languages;
        });

        this.profileService.getProfileInfo().then((profileInfo) => {
            this.inProduction = profileInfo.inProduction;
            this.swaggerEnabled = profileInfo.swaggerEnabled;
        });

        this.loadDepartments();
    }

    changeLanguage(languageKey: string) {
      this.languageService.changeLanguage(languageKey);
    }

    isAuthenticated() {
        return this.principal.isAuthenticated();
    }

    login() {
        this.modalRef = this.loginModalService.open();
    }

    logout() {
        this.loginService.logout();
        this.router.navigate(['']);
    }

    loadDepartments() {
        this.departmentInfoService.query().subscribe(
            (res: HttpResponse<DepartmentInfo[]>) => this.departments = res.body,
            (res: HttpErrorResponse) => this.onLoadDepartmentsInfoError(res)
        );
    }

    private onLoadDepartmentsInfoError(response) {
        if (response.status >= 500 && response.status < 600) {
            this.jhiAlertService.error('error.internalServerError', null, null);
        } else {
            this.jhiAlertService.error(response.error.message, null, null);
        }
    }

    trackDepartmentById(index: number, item: DepartmentInfo) {
        return item.id;
    }

    trackStudyFieldById(index: number, item: StudyField) {
        return item.id;
    }
}
