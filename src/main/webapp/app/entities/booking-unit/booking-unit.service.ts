import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {SERVER_API_URL} from '../../app.constants';
import {JhiDateUtils} from 'ng-jhipster';
import {createRequestOption, BookingUnit} from '../../shared';

export type EntityResponseType = HttpResponse<BookingUnit>;

@Injectable()
export class BookingUnitService {

    private resourceUrl =  SERVER_API_URL + 'api/booking-units';

    constructor(private http: HttpClient, private dateUtils: JhiDateUtils) { }

    create(bookingUnit: BookingUnit): Observable<EntityResponseType> {
        const copy = this.convert(bookingUnit);
        return this.http.post<BookingUnit>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    update(bookingUnit: BookingUnit): Observable<EntityResponseType> {
        const copy = this.convert(bookingUnit);
        return this.http.put<BookingUnit>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<BookingUnit>(`${this.resourceUrl}/${id}`, { observe: 'response'})
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    query(req?: any, filter?: any): Observable<HttpResponse<BookingUnit[]>> {
        const options = createRequestOption(req, filter);
        return this.http.get<BookingUnit[]>(this.resourceUrl, { params: options, observe: 'response' })
            .map((res: HttpResponse<BookingUnit[]>) => this.convertArrayResponse(res));
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response'});
    }

    private convertResponse(res: EntityResponseType): EntityResponseType {
        const body: BookingUnit = this.convertItemFromServer(res.body);
        return res.clone({body});
    }

    private convertArrayResponse(res: HttpResponse<BookingUnit[]>): HttpResponse<BookingUnit[]> {
        const jsonResponse: BookingUnit[] = res.body;
        const body: BookingUnit[] = [];
        for (let i = 0; i < jsonResponse.length; i++) {
            body.push(this.convertItemFromServer(jsonResponse[i]));
        }
        return res.clone({body});
    }

    /**
     * Convert a returned JSON object to BookingUnit.
     */
    private convertItemFromServer(bookingUnit: BookingUnit): BookingUnit {
        return Object.assign({}, bookingUnit);
    }

    /**
     * Convert a BookingUnit to a JSON which can be sent to the server.
     */
    private convert(bookingUnit: BookingUnit): BookingUnit {
        return Object.assign({}, bookingUnit);
    }
}
