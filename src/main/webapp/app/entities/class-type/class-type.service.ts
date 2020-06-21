import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {SERVER_API_URL} from '../../app.constants';
import {createRequestOption, ClassType} from '../../shared';

export type EntityResponseType = HttpResponse<ClassType>;

@Injectable()
export class ClassTypeService {

    private resourceUrl =  SERVER_API_URL + 'api/class-types';

    constructor(private http: HttpClient) { }

    create(classType: ClassType): Observable<EntityResponseType> {
        const copy = this.convert(classType);
        return this.http.post<ClassType>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    update(classType: ClassType): Observable<EntityResponseType> {
        const copy = this.convert(classType);
        return this.http.put<ClassType>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<ClassType>(`${this.resourceUrl}/${id}`, { observe: 'response'})
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    query(req?: any): Observable<HttpResponse<ClassType[]>> {
        const options = createRequestOption(req);
        return this.http.get<ClassType[]>(this.resourceUrl, { params: options, observe: 'response' })
            .map((res: HttpResponse<ClassType[]>) => this.convertArrayResponse(res));
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response'});
    }

    private convertResponse(res: EntityResponseType): EntityResponseType {
        const body: ClassType = this.convertItemFromServer(res.body);
        return res.clone({body});
    }

    private convertArrayResponse(res: HttpResponse<ClassType[]>): HttpResponse<ClassType[]> {
        const jsonResponse: ClassType[] = res.body;
        const body: ClassType[] = [];
        for (let i = 0; i < jsonResponse.length; i++) {
            body.push(this.convertItemFromServer(jsonResponse[i]));
        }
        return res.clone({body});
    }

    /**
     * Convert a returned JSON object to ClassType.
     */
    private convertItemFromServer(classType: ClassType): ClassType {
        return Object.assign({}, classType);
    }

    /**
     * Convert a ClassType to a JSON which can be sent to the server.
     */
    private convert(classType: ClassType): ClassType {
        return Object.assign({}, classType);
    }
}
