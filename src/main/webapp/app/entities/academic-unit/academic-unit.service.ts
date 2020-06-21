import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {SERVER_API_URL} from '../../app.constants';
import {createRequestOption, AcademicUnit} from '../../shared';

export type EntityResponseType = HttpResponse<AcademicUnit>;

@Injectable()
export class AcademicUnitService {

    private resourceUrl =  SERVER_API_URL + 'api/academic-units';

    constructor(private http: HttpClient) { }

    create(academicUnit: AcademicUnit): Observable<EntityResponseType> {
        const copy = this.convert(academicUnit);
        return this.http.post<AcademicUnit>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    update(academicUnit: AcademicUnit): Observable<EntityResponseType> {
        const copy = this.convert(academicUnit);
        return this.http.put<AcademicUnit>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<AcademicUnit>(`${this.resourceUrl}/${id}`, { observe: 'response'})
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    query(req?: any): Observable<HttpResponse<AcademicUnit[]>> {
        const options = createRequestOption(req);
        return this.http.get<AcademicUnit[]>(this.resourceUrl, { params: options, observe: 'response' })
            .map((res: HttpResponse<AcademicUnit[]>) => this.convertArrayResponse(res));
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response'});
    }

    private convertResponse(res: EntityResponseType): EntityResponseType {
        const body: AcademicUnit = this.convertItemFromServer(res.body);
        return res.clone({body});
    }

    private convertArrayResponse(res: HttpResponse<AcademicUnit[]>): HttpResponse<AcademicUnit[]> {
        const jsonResponse: AcademicUnit[] = res.body;
        const body: AcademicUnit[] = [];
        for (let i = 0; i < jsonResponse.length; i++) {
            body.push(this.convertItemFromServer(jsonResponse[i]));
        }
        return res.clone({body});
    }

    /**
     * Convert a returned JSON object to AcademicUnit.
     */
    private convertItemFromServer(academicUnit: AcademicUnit): AcademicUnit {
        return Object.assign({}, academicUnit);
    }

    /**
     * Convert a AcademicUnit to a JSON which can be sent to the server.
     */
    private convert(academicUnit: AcademicUnit): AcademicUnit {
        return Object.assign({}, academicUnit);
    }
}
