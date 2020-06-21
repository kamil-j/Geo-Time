import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { SERVER_API_URL } from '../../app.constants';
import { createRequestOption, Subdepartment } from '../../shared';

export type EntityResponseType = HttpResponse<Subdepartment>;

@Injectable()
export class SubdepartmentService {

    private resourceUrl =  SERVER_API_URL + 'api/subdepartments';

    constructor(private http: HttpClient) { }

    create(subdepartment: Subdepartment): Observable<EntityResponseType> {
        const copy = this.convert(subdepartment);
        return this.http.post<Subdepartment>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    update(subdepartment: Subdepartment): Observable<EntityResponseType> {
        const copy = this.convert(subdepartment);
        return this.http.put<Subdepartment>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<Subdepartment>(`${this.resourceUrl}/${id}`, { observe: 'response'})
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    query(req?: any): Observable<HttpResponse<Subdepartment[]>> {
        const options = createRequestOption(req);
        return this.http.get<Subdepartment[]>(this.resourceUrl, { params: options, observe: 'response' })
            .map((res: HttpResponse<Subdepartment[]>) => this.convertArrayResponse(res));
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response'});
    }

    private convertResponse(res: EntityResponseType): EntityResponseType {
        const body: Subdepartment = this.convertItemFromServer(res.body);
        return res.clone({body});
    }

    private convertArrayResponse(res: HttpResponse<Subdepartment[]>): HttpResponse<Subdepartment[]> {
        const jsonResponse: Subdepartment[] = res.body;
        const body: Subdepartment[] = [];
        for (let i = 0; i < jsonResponse.length; i++) {
            body.push(this.convertItemFromServer(jsonResponse[i]));
        }
        return res.clone({body});
    }

    /**
     * Convert a returned JSON object to Subdepartment.
     */
    private convertItemFromServer(subdepartment: Subdepartment): Subdepartment {
        const copy: Subdepartment = Object.assign({}, subdepartment);
        return copy;
    }

    /**
     * Convert a Subdepartment to a JSON which can be sent to the server.
     */
    private convert(subdepartment: Subdepartment): Subdepartment {
        const copy: Subdepartment = Object.assign({}, subdepartment);
        return copy;
    }
}
