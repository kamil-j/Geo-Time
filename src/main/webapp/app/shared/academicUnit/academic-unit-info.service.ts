import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {SERVER_API_URL} from '../../app.constants';

import {AcademicUnitGroupsInfo, createRequestOption} from '..';
import {AcademicUnitInfo} from './academic-unit-info.model';

type EntityResponseType = HttpResponse<AcademicUnitInfo>;

@Injectable()
export class AcademicUnitInfoService {

    private resourceUrl =  SERVER_API_URL + '/api/academic-units-info';

    constructor(private http: HttpClient) { }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<AcademicUnitInfo>(`${this.resourceUrl}/${id}`, { observe: 'response'})
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    private convertResponse(res: EntityResponseType): EntityResponseType {
        const body: AcademicUnitInfo = this.convertItemFromServer(res.body);
        return res.clone({body});
    }

    findGroups(id: number): Observable<HttpResponse<AcademicUnitGroupsInfo>> {
        return this.http.get<AcademicUnitGroupsInfo>(`${this.resourceUrl}/${id}/groups`, { observe: 'response'})
            .map((res: HttpResponse<AcademicUnitGroupsInfo>) => this.convertAcademicUnitGroupsResponse(res));
    }

    private convertAcademicUnitGroupsResponse(res: HttpResponse<AcademicUnitGroupsInfo>): HttpResponse<AcademicUnitGroupsInfo> {
        const body: AcademicUnitGroupsInfo = Object.assign({}, res.body);
        return res.clone({body});
    }

    query(req?: any): Observable<HttpResponse<AcademicUnitInfo[]>> {
        const options = createRequestOption(req);
        return this.http.get<AcademicUnitInfo[]>(this.resourceUrl, { params: options, observe: 'response' })
            .map((res: HttpResponse<AcademicUnitInfo[]>) => this.convertArrayResponse(res));
    }

    private convertArrayResponse(res: HttpResponse<AcademicUnitInfo[]>): HttpResponse<AcademicUnitInfo[]> {
        const jsonResponse: AcademicUnitInfo[] = res.body;
        const body: AcademicUnitInfo[] = [];
        for (let i = 0; i < jsonResponse.length; i++) {
            body.push(this.convertItemFromServer(jsonResponse[i]));
        }
        return res.clone({body});
    }

    /**
     * Convert a returned JSON object to AcademicUnitInfo.
     */
    private convertItemFromServer(academicUnitInfo: AcademicUnitInfo): AcademicUnitInfo {
        return Object.assign({}, academicUnitInfo);
    }
}
