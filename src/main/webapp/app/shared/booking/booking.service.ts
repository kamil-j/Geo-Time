import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {SERVER_API_URL} from '../../app.constants';
import {Booking} from './booking.model';
import {BookingLock} from './booking-lock.model';

@Injectable()
export class BookingService {

    private resourceUrl =  SERVER_API_URL + '/api/timetable-bookings';

    constructor(private http: HttpClient) { }

    updateBooking(booking: Booking): Observable<HttpResponse<Booking>> {
        const copy = this.convertBooking(booking);
        return this.http.put<Booking>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: HttpResponse<Booking>) => this.convertResponse(res));
    }

    createBooking(booking: Booking): Observable<HttpResponse<Booking>> {
        const copy = this.convertBooking(booking);
        return this.http.post<Booking>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: HttpResponse<Booking>) => this.convertResponse(res));
    }

    private convertBooking(booking: Booking): Booking {
        return Object.assign({}, booking);
    }

    private convertResponse(res: HttpResponse<Booking>): HttpResponse<Booking> {
        const body: Booking = this.convertItemFromServer(res.body);
        return res.clone({body});
    }

    private convertItemFromServer(booking: Booking): Booking {
        return Object.assign({}, booking);
    }

    removeBooking(id: number, userId?: number): Observable<HttpResponse<any>> {
        const userIdParam = userId ? '?userId=' + userId : '';
        return this.http.delete<any>(`/api/timetable-bookings/${id}` + userIdParam, { observe: 'response'});
    }

    lockBooking(bookingLock: BookingLock): Observable<HttpResponse<Booking>> {
        return this.http.put<BookingLock>(`${this.resourceUrl}/lock`, bookingLock, { observe: 'response' })
            .map((res: HttpResponse<Booking>) => this.convertResponse(res));
    }
}
