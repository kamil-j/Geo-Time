import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs/Subscription';
import { JhiEventManager, JhiParseLinks, JhiAlertService } from 'ng-jhipster';
import { BookingUnitService } from './booking-unit.service';
import {ITEMS_PER_PAGE, Principal, BookingUnit, Subdepartment, MANAGER_AUTHORITY} from '../../shared';
import {SubdepartmentService} from '../subdepartment';

@Component({
    selector: 'jhi-booking-unit',
    templateUrl: './booking-unit.component.html'
})
export class BookingUnitComponent implements OnInit, OnDestroy {

    currentAccount: any;
    bookingUnits: BookingUnit[];
    error: any;
    success: any;
    eventSubscriber: Subscription;
    routeData: any;
    links: any;
    totalItems: any;
    queryCount: any;
    itemsPerPage: any;
    page: any;
    predicate: any;
    previousPage: any;
    reverse: any;
    filteringOptions: Subdepartment[];
    filteringOptionsId: number[];

    constructor(
        private bookingUnitService: BookingUnitService,
        private subdepartmentService: SubdepartmentService,
        private parseLinks: JhiParseLinks,
        private jhiAlertService: JhiAlertService,
        private principal: Principal,
        private activatedRoute: ActivatedRoute,
        private router: Router,
        private eventManager: JhiEventManager
    ) {
        this.itemsPerPage = ITEMS_PER_PAGE;
        this.routeData = this.activatedRoute.data.subscribe((data) => {
            this.page = data.pagingParams.page;
            this.previousPage = data.pagingParams.page;
            this.reverse = data.pagingParams.ascending;
            this.predicate = data.pagingParams.predicate;
        });
    }

    loadAll() {
        let filterOptions = null;
        if (this.filteringOptionsId && this.filteringOptionsId.length > 0) {
            filterOptions = {'subdepartmentId.in': this.filteringOptionsId.join(',')};
        }
        this.bookingUnitService.query({
            page: this.page - 1,
            size: this.itemsPerPage,
            sort: this.sort()}, filterOptions).subscribe(
                (res: HttpResponse<BookingUnit[]>) => this.onSuccess(res.body, res.headers),
                (res: HttpErrorResponse) => this.onError(res.message)
        );
    }
    loadPage(page: number) {
        if (page !== this.previousPage) {
            this.previousPage = page;
            this.transition();
        }
    }
    transition() {
        this.router.navigate(['/booking-unit'], {queryParams:
            {
                page: this.page,
                size: this.itemsPerPage,
                sort: this.predicate + ',' + (this.reverse ? 'asc' : 'desc')
            }
        });
        this.loadAll();
    }

    clear() {
        this.page = 0;
        this.router.navigate(['/booking-unit', {
            page: this.page,
            sort: this.predicate + ',' + (this.reverse ? 'asc' : 'desc')
        }]);
        this.loadAll();
    }
    ngOnInit() {
        this.loadAll();
        this.principal.identity().then((account) => {
            this.currentAccount = account;
            if (this.currentAccount.authorities.includes(MANAGER_AUTHORITY)) {
                this.loadFilteringOptions();
            }
        });
        this.registerChangeInBookingUnits();
    }

    loadFilteringOptions() {
        this.subdepartmentService.query().subscribe((
            res: HttpResponse<Subdepartment[]>) => {
                this.filteringOptions = res.body;
                this.filteringOptionsId = this.filteringOptions.map((filterOption) => filterOption.id);
            }
        );
    }

    filter(id: number) {
        if (this.filteringOptionsId.find((idFromList) => idFromList === id)) {
            this.filteringOptionsId = this.filteringOptionsId.filter((idFromList) => idFromList !== id);
        } else {
            this.filteringOptionsId.push(id);
        }
        if (this.filteringOptionsId.length > 0) {
            this.page = 1;
            this.loadAll();
        } else {
            this.bookingUnits = [];
        }
    }

    ngOnDestroy() {
        this.eventManager.destroy(this.eventSubscriber);
    }

    trackId(index: number, item: BookingUnit) {
        return item.id;
    }
    registerChangeInBookingUnits() {
        this.eventSubscriber = this.eventManager.subscribe('bookingUnitListModification', (response) => this.loadAll());
    }

    sort() {
        const result = [this.predicate + ',' + (this.reverse ? 'asc' : 'desc')];
        if (this.predicate !== 'id') {
            result.push('id');
        }
        return result;
    }

    private onSuccess(data, headers) {
        this.links = this.parseLinks.parse(headers.get('link'));
        this.totalItems = headers.get('X-Total-Count');
        this.queryCount = this.totalItems;
        // this.page = pagingParams.page;
        this.bookingUnits = data;
    }
    private onError(error) {
        this.jhiAlertService.error(error.message, null, null);
    }
}
