import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs/Subscription';
import { JhiEventManager } from 'ng-jhipster';
import { RoomType } from '../../shared';
import { RoomTypeService } from './room-type.service';

@Component({
    selector: 'jhi-room-type-detail',
    templateUrl: './room-type-detail.component.html'
})
export class RoomTypeDetailComponent implements OnInit, OnDestroy {

    roomType: RoomType;
    private subscription: Subscription;
    private eventSubscriber: Subscription;

    constructor(
        private eventManager: JhiEventManager,
        private roomTypeService: RoomTypeService,
        private route: ActivatedRoute
    ) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInRoomTypes();
    }

    load(id) {
        this.roomTypeService.find(id)
            .subscribe((roomTypeResponse: HttpResponse<RoomType>) => {
                this.roomType = roomTypeResponse.body;
            });
    }
    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    registerChangeInRoomTypes() {
        this.eventSubscriber = this.eventManager.subscribe(
            'roomTypeListModification',
            (response) => this.load(this.roomType.id)
        );
    }
}
