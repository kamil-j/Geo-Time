import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs/Subscription';
import { JhiEventManager } from 'ng-jhipster';
import { BookingUnit } from '../../shared';
import { BookingUnitService } from './booking-unit.service';

@Component({
    selector: 'jhi-booking-unit-detail',
    templateUrl: './booking-unit-detail.component.html'
})
export class BookingUnitDetailComponent implements OnInit, OnDestroy {

    bookingUnit: BookingUnit;
    private subscription: Subscription;
    private eventSubscriber: Subscription;

    constructor(
        private eventManager: JhiEventManager,
        private bookingUnitService: BookingUnitService,
        private route: ActivatedRoute
    ) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInBookingUnits();
    }

    load(id) {
        this.bookingUnitService.find(id)
            .subscribe((bookingUnitResponse: HttpResponse<BookingUnit>) => {
                this.bookingUnit = bookingUnitResponse.body;
            });
    }
    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    registerChangeInBookingUnits() {
        this.eventSubscriber = this.eventManager.subscribe(
            'bookingUnitListModification',
            (response) => this.load(this.bookingUnit.id)
        );
    }
}
