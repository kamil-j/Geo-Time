import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { SERVER_API_URL } from '../../app.constants';
import { JhiDateUtils } from 'ng-jhipster';
import { createRequestOption, SchedulingTimeFrame } from '../../shared';

export type EntityResponseType = HttpResponse<SchedulingTimeFrame>;

@Injectable()
export class SchedulingTimeFrameService {

    private resourceUrl =  SERVER_API_URL + 'api/scheduling-time-frames';

    constructor(private http: HttpClient, private dateUtils: JhiDateUtils) { }

    create(schedulingTimeFrame: SchedulingTimeFrame): Observable<EntityResponseType> {
        const copy = this.convert(schedulingTimeFrame);
        return this.http.post<SchedulingTimeFrame>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    update(schedulingTimeFrame: SchedulingTimeFrame): Observable<EntityResponseType> {
        const copy = this.convert(schedulingTimeFrame);
        return this.http.put<SchedulingTimeFrame>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<SchedulingTimeFrame>(`${this.resourceUrl}/${id}`, { observe: 'response'})
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    findForUser(userId: number): Observable<HttpResponse<SchedulingTimeFrame>> {
        return this.http.get<SchedulingTimeFrame>(`${this.resourceUrl}/user/${userId}`, { observe: 'response'})
            .map((res: HttpResponse<SchedulingTimeFrame>) => this.convertResponse(res));
    }

    query(req?: any): Observable<HttpResponse<SchedulingTimeFrame[]>> {
        const options = createRequestOption(req);
        return this.http.get<SchedulingTimeFrame[]>(this.resourceUrl, { params: options, observe: 'response' })
            .map((res: HttpResponse<SchedulingTimeFrame[]>) => this.convertArrayResponse(res));
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response'});
    }

    private convertResponse(res: EntityResponseType): EntityResponseType {
        const body: SchedulingTimeFrame = this.convertItemFromServer(res.body);
        return res.clone({body});
    }

    private convertArrayResponse(res: HttpResponse<SchedulingTimeFrame[]>): HttpResponse<SchedulingTimeFrame[]> {
        const jsonResponse: SchedulingTimeFrame[] = res.body;
        const body: SchedulingTimeFrame[] = [];
        for (let i = 0; i < jsonResponse.length; i++) {
            body.push(this.convertItemFromServer(jsonResponse[i]));
        }
        return res.clone({body});
    }

    /**
     * Convert a returned JSON object to SchedulingTimeFrame.
     */
    private convertItemFromServer(schedulingTimeFrame: SchedulingTimeFrame): SchedulingTimeFrame {
        if (schedulingTimeFrame == null) {
            return null;
        }
        const copy: SchedulingTimeFrame = Object.assign({}, schedulingTimeFrame);
        copy.startTime = this.dateUtils
            .convertDateTimeFromServer(schedulingTimeFrame.startTime);
        copy.endTime = this.dateUtils
            .convertDateTimeFromServer(schedulingTimeFrame.endTime);
        return copy;
    }

    /**
     * Convert a SchedulingTimeFrame to a JSON which can be sent to the server.
     */
    private convert(schedulingTimeFrame: SchedulingTimeFrame): SchedulingTimeFrame {
        const copy: SchedulingTimeFrame = Object.assign({}, schedulingTimeFrame);

        copy.startTime = this.dateUtils.toDate(schedulingTimeFrame.startTime);

        copy.endTime = this.dateUtils.toDate(schedulingTimeFrame.endTime);
        return copy;
    }
}
