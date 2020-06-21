import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {DepartmentPlanningMetrics} from './department-planning-metrics.model';
import {SERVER_API_URL} from '../../app.constants';
import {Observable} from 'rxjs/Observable';

@Injectable()
export class DepartmentPlanningMetricsService {

    private resourceUrl =  SERVER_API_URL + 'api/planning-metrics';

    private static convertResponse(res) {
        const body = Object.assign({}, res.body);
        return res.clone({body});
    }

    constructor(private http: HttpClient) {}

    getDepartmentPlanningMetrics(): Observable<HttpResponse<DepartmentPlanningMetrics>> {
        return this.http.get<DepartmentPlanningMetrics>(`${this.resourceUrl}/department`, { observe: 'response' })
            .map((res: HttpResponse<DepartmentPlanningMetrics>) => DepartmentPlanningMetricsService.convertResponse(res));
    }
}
