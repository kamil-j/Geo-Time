import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { SERVER_API_URL } from '../../app.constants';
import { JhiDateUtils } from 'ng-jhipster';
import { createRequestOption, ScheduleUnit } from '../../shared';

export type EntityResponseType = HttpResponse<ScheduleUnit>;

@Injectable()
export class ScheduleUnitService {

    private resourceUrl =  SERVER_API_URL + 'api/schedule-units';

    constructor(private http: HttpClient, private dateUtils: JhiDateUtils) { }

    create(scheduleUnit: ScheduleUnit): Observable<EntityResponseType> {
        const copy = this.convert(scheduleUnit);
        return this.http.post<ScheduleUnit>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    update(scheduleUnit: ScheduleUnit): Observable<EntityResponseType> {
        const copy = this.convert(scheduleUnit);
        return this.http.put<ScheduleUnit>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<ScheduleUnit>(`${this.resourceUrl}/${id}`, { observe: 'response'})
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    query(req?: any, filter?: any): Observable<HttpResponse<ScheduleUnit[]>> {
        const options = createRequestOption(req, filter);
        return this.http.get<ScheduleUnit[]>(this.resourceUrl, { params: options, observe: 'response' })
            .map((res: HttpResponse<ScheduleUnit[]>) => this.convertArrayResponse(res));
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response'});
    }

    private convertResponse(res: EntityResponseType): EntityResponseType {
        const body: ScheduleUnit = this.convertItemFromServer(res.body);
        return res.clone({body});
    }

    private convertArrayResponse(res: HttpResponse<ScheduleUnit[]>): HttpResponse<ScheduleUnit[]> {
        const jsonResponse: ScheduleUnit[] = res.body;
        const body: ScheduleUnit[] = [];
        for (let i = 0; i < jsonResponse.length; i++) {
            body.push(this.convertItemFromServer(jsonResponse[i]));
        }
        return res.clone({body});
    }

    /**
     * Convert a returned JSON object to ScheduleUnit.
     */
    private convertItemFromServer(scheduleUnit: ScheduleUnit): ScheduleUnit {
        const copy: ScheduleUnit = Object.assign({}, scheduleUnit);
        copy.startDate = this.dateUtils
            .convertDateTimeFromServer(scheduleUnit.startDate);
        copy.endDate = this.dateUtils
            .convertDateTimeFromServer(scheduleUnit.endDate);
        return copy;
    }

    /**
     * Convert a ScheduleUnit to a JSON which can be sent to the server.
     */
    private convert(scheduleUnit: ScheduleUnit): ScheduleUnit {
        const copy: ScheduleUnit = Object.assign({}, scheduleUnit);

        copy.startDate = this.dateUtils.toDate(scheduleUnit.startDate);

        copy.endDate = this.dateUtils.toDate(scheduleUnit.endDate);
        return copy;
    }
}
