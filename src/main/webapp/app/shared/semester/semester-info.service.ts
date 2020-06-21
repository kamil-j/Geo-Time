import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {SERVER_API_URL} from '../../app.constants';

import {SemesterInfo} from './semester-info.model';

type EntityResponseType = HttpResponse<SemesterInfo>;

@Injectable()
export class SemesterInfoService {

    private resourceUrl =  SERVER_API_URL + '/api/semester-info';

    constructor(private http: HttpClient) { }

    getCurrentSemester(): Observable<EntityResponseType> {
        return this.http.get<SemesterInfo>(`${this.resourceUrl}/current`, { observe: 'response'})
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    private convertResponse(res: EntityResponseType): EntityResponseType {
        const body: SemesterInfo = this.convertItemFromServer(res.body);
        return res.clone({body});
    }

    /**
     * Convert a returned JSON object to DepartmentInfo.
     */
    private convertItemFromServer(semesterInfo: SemesterInfo): SemesterInfo {
        return Object.assign({}, semesterInfo);
    }
}
