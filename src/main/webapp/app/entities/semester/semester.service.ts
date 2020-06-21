import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { SERVER_API_URL } from '../../app.constants';
import { JhiDateUtils } from 'ng-jhipster';
import { createRequestOption, Semester } from '../../shared';

export type EntityResponseType = HttpResponse<Semester>;

@Injectable()
export class SemesterService {

    private resourceUrl =  SERVER_API_URL + 'api/semesters';

    constructor(private http: HttpClient, private dateUtils: JhiDateUtils) { }

    create(semester: Semester): Observable<EntityResponseType> {
        const copy = this.convert(semester);
        return this.http.post<Semester>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    update(semester: Semester): Observable<EntityResponseType> {
        const copy = this.convert(semester);
        return this.http.put<Semester>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<Semester>(`${this.resourceUrl}/${id}`, { observe: 'response'})
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    query(req?: any): Observable<HttpResponse<Semester[]>> {
        const options = createRequestOption(req);
        return this.http.get<Semester[]>(this.resourceUrl, { params: options, observe: 'response' })
            .map((res: HttpResponse<Semester[]>) => this.convertArrayResponse(res));
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response'});
    }

    private convertResponse(res: EntityResponseType): EntityResponseType {
        const body: Semester = this.convertItemFromServer(res.body);
        return res.clone({body});
    }

    private convertArrayResponse(res: HttpResponse<Semester[]>): HttpResponse<Semester[]> {
        const jsonResponse: Semester[] = res.body;
        const body: Semester[] = [];
        for (let i = 0; i < jsonResponse.length; i++) {
            body.push(this.convertItemFromServer(jsonResponse[i]));
        }
        return res.clone({body});
    }

    /**
     * Convert a returned JSON object to Semester.
     */
    private convertItemFromServer(semester: Semester): Semester {
        const copy: Semester = Object.assign({}, semester);
        copy.startDate = this.dateUtils
            .convertDateTimeFromServer(semester.startDate);
        copy.endDate = this.dateUtils
            .convertDateTimeFromServer(semester.endDate);
        return copy;
    }

    /**
     * Convert a Semester to a JSON which can be sent to the server.
     */
    private convert(semester: Semester): Semester {
        const copy: Semester = Object.assign({}, semester);

        copy.startDate = this.dateUtils.toDate(semester.startDate);

        copy.endDate = this.dateUtils.toDate(semester.endDate);
        return copy;
    }
}
