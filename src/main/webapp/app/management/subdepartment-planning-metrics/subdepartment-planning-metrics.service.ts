import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {SERVER_API_URL} from '../../app.constants';
import {Observable} from 'rxjs/Observable';
import {SubdepartmentPlanningMetrics} from './subdepartment-planning-metrics.model';

@Injectable()
export class SubdepartmentPlanningMetricsService {

    private resourceUrl =  SERVER_API_URL + 'api/planning-metrics';

    private static convertResponse(res) {
        const body = Object.assign({}, res.body);
        return res.clone({body});
    }

    constructor(private http: HttpClient) {}

    getPlanningMetrics(id: number): Observable<HttpResponse<SubdepartmentPlanningMetrics>> {
        const urlWithSubdepartmentId = id ? '/' + id : '';
        return this.http.get<SubdepartmentPlanningMetrics>(`${this.resourceUrl}/subdepartment${urlWithSubdepartmentId}`, { observe: 'response' })
            .map((res: HttpResponse<SubdepartmentPlanningMetrics>) => SubdepartmentPlanningMetricsService.convertResponse(res));
    }
}
