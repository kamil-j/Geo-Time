import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {SERVER_API_URL} from '../../app.constants';

import {createRequestOption} from '../../shared';
import {DepartmentInfo} from './department-info.model';

export type EntityResponseType = HttpResponse<DepartmentInfo>;

@Injectable()
export class DepartmentInfoService {

    private resourceUrl =  SERVER_API_URL + '/api/departments-info';

    constructor(private http: HttpClient) { }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<DepartmentInfo>(`${this.resourceUrl}/${id}`, { observe: 'response'})
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    private convertResponse(res: EntityResponseType): EntityResponseType {
        const body: DepartmentInfo = this.convertItemFromServer(res.body);
        return res.clone({body});
    }

    query(req?: any): Observable<HttpResponse<DepartmentInfo[]>> {
        const options = createRequestOption(req);
        return this.http.get<DepartmentInfo[]>(this.resourceUrl, { params: options, observe: 'response' })
            .map((res: HttpResponse<DepartmentInfo[]>) => this.convertArrayResponse(res));
    }

    private convertArrayResponse(res: HttpResponse<DepartmentInfo[]>): HttpResponse<DepartmentInfo[]> {
        const jsonResponse: DepartmentInfo[] = res.body;
        const body: DepartmentInfo[] = [];
        for (let i = 0; i < jsonResponse.length; i++) {
            body.push(this.convertItemFromServer(jsonResponse[i]));
        }
        return res.clone({body});
    }

    /**
     * Convert a returned JSON object to DepartmentInfo.
     */
    private convertItemFromServer(departmentInfo: DepartmentInfo): DepartmentInfo {
        return Object.assign({}, departmentInfo);
    }
}
