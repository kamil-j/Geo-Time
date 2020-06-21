import {Injectable} from '@angular/core';
import {HttpClient, HttpParams, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {SERVER_API_URL} from '../../app.constants';
import {createRequestOption, ClassUnit} from '../../shared';

export type EntityResponseType = HttpResponse<ClassUnit>;

@Injectable()
export class ClassUnitService {

    private resourceUrl =  SERVER_API_URL + 'api/class-units';

    constructor(private http: HttpClient) { }

    create(classUnit: ClassUnit): Observable<EntityResponseType> {
        const copy = this.convert(classUnit);
        return this.http.post<ClassUnit>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    update(classUnit: ClassUnit): Observable<EntityResponseType> {
        const copy = this.convert(classUnit);
        return this.http.put<ClassUnit>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    assign(classUnitId: number, userId: number): Observable<any> {
        return this.http.put<ClassUnit>(`${this.resourceUrl}/assign`, null, {
            params: new HttpParams().set('classUnitId', classUnitId.toString()).set('userId', userId.toString()),
            observe: 'response'
        });
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<ClassUnit>(`${this.resourceUrl}/${id}`, { observe: 'response'})
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    query(req?: any, filter?: any): Observable<HttpResponse<ClassUnit[]>> {
        const options = createRequestOption(req, filter);
        return this.http.get<ClassUnit[]>(this.resourceUrl, { params: options, observe: 'response' })
            .map((res: HttpResponse<ClassUnit[]>) => this.convertArrayResponse(res));
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response'});
    }

    uploadFile(file: File, subdepartmentId: number): Observable<HttpResponse<any>> {
        const formData: FormData = new FormData();
        formData.append('file', file);
        formData.append('subdepartmentId', subdepartmentId.toString());

        return this.http.post<File>(`${this.resourceUrl}/upload`, formData, { observe: 'response' });
    }

    private convertResponse(res: EntityResponseType): EntityResponseType {
        const body: ClassUnit = this.convertItemFromServer(res.body);
        return res.clone({body});
    }

    private convertArrayResponse(res: HttpResponse<ClassUnit[]>): HttpResponse<ClassUnit[]> {
        const jsonResponse: ClassUnit[] = res.body;
        const body: ClassUnit[] = [];
        for (let i = 0; i < jsonResponse.length; i++) {
            body.push(this.convertItemFromServer(jsonResponse[i]));
        }
        return res.clone({body});
    }

    /**
     * Convert a returned JSON object to ClassUnit.
     */
    private convertItemFromServer(classUnit: ClassUnit): ClassUnit {
        return Object.assign({}, classUnit);
    }

    /**
     * Convert a ClassUnit to a JSON which can be sent to the server.
     */
    private convert(classUnit: ClassUnit): ClassUnit {
        return Object.assign({}, classUnit);
    }
}
