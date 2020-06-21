import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {SERVER_API_URL} from '../../app.constants';
import {createRequestOption, ClassUnitGroup} from '../../shared';

export type EntityResponseType = HttpResponse<ClassUnitGroup>;

@Injectable()
export class ClassUnitGroupService {

    private resourceUrl =  SERVER_API_URL + 'api/class-unit-groups';

    constructor(private http: HttpClient) { }

    create(classUnitGroup: ClassUnitGroup): Observable<EntityResponseType> {
        const copy = this.convert(classUnitGroup);
        return this.http.post<ClassUnitGroup>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    update(classUnitGroup: ClassUnitGroup): Observable<EntityResponseType> {
        const copy = this.convert(classUnitGroup);
        return this.http.put<ClassUnitGroup>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<ClassUnitGroup>(`${this.resourceUrl}/${id}`, { observe: 'response'})
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    query(req?: any): Observable<HttpResponse<ClassUnitGroup[]>> {
        const options = createRequestOption(req);
        return this.http.get<ClassUnitGroup[]>(this.resourceUrl, { params: options, observe: 'response' })
            .map((res: HttpResponse<ClassUnitGroup[]>) => this.convertArrayResponse(res));
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response'});
    }

    private convertResponse(res: EntityResponseType): EntityResponseType {
        const body: ClassUnitGroup = this.convertItemFromServer(res.body);
        return res.clone({body});
    }

    private convertArrayResponse(res: HttpResponse<ClassUnitGroup[]>): HttpResponse<ClassUnitGroup[]> {
        const jsonResponse: ClassUnitGroup[] = res.body;
        const body: ClassUnitGroup[] = [];
        for (let i = 0; i < jsonResponse.length; i++) {
            body.push(this.convertItemFromServer(jsonResponse[i]));
        }
        return res.clone({body});
    }

    /**
     * Convert a returned JSON object to ClassUnitGroup.
     */
    private convertItemFromServer(classUnitGroup: ClassUnitGroup): ClassUnitGroup {
        return Object.assign({}, classUnitGroup);
    }

    /**
     * Convert a ClassUnitGroup to a JSON which can be sent to the server.
     */
    private convert(classUnitGroup: ClassUnitGroup): ClassUnitGroup {
        return Object.assign({}, classUnitGroup);
    }
}
