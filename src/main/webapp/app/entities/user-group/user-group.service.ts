import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {SERVER_API_URL} from '../../app.constants';
import {createRequestOption, UserGroup} from '../../shared';

export type EntityResponseType = HttpResponse<UserGroup>;

@Injectable()
export class UserGroupService {

    private resourceUrl =  SERVER_API_URL + 'api/user-groups';

    constructor(private http: HttpClient) { }

    create(userGroup: UserGroup): Observable<EntityResponseType> {
        const copy = this.convert(userGroup);
        return this.http.post<UserGroup>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    update(userGroup: UserGroup): Observable<EntityResponseType> {
        const copy = this.convert(userGroup);
        return this.http.put<UserGroup>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<UserGroup>(`${this.resourceUrl}/${id}`, { observe: 'response'})
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    query(req?: any): Observable<HttpResponse<UserGroup[]>> {
        const options = createRequestOption(req);
        return this.http.get<UserGroup[]>(this.resourceUrl, { params: options, observe: 'response' })
            .map((res: HttpResponse<UserGroup[]>) => this.convertArrayResponse(res));
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response'});
    }

    private convertResponse(res: EntityResponseType): EntityResponseType {
        const body: UserGroup = this.convertItemFromServer(res.body);
        return res.clone({body});
    }

    private convertArrayResponse(res: HttpResponse<UserGroup[]>): HttpResponse<UserGroup[]> {
        const jsonResponse: UserGroup[] = res.body;
        const body: UserGroup[] = [];
        for (let i = 0; i < jsonResponse.length; i++) {
            body.push(this.convertItemFromServer(jsonResponse[i]));
        }
        return res.clone({body});
    }

    /**
     * Convert a returned JSON object to UserGroup.
     */
    private convertItemFromServer(userGroup: UserGroup): UserGroup {
        return Object.assign({}, userGroup);
    }

    /**
     * Convert a UserGroup to a JSON which can be sent to the server.
     */
    private convert(userGroup: UserGroup): UserGroup {
        return Object.assign({}, userGroup);
    }
}
