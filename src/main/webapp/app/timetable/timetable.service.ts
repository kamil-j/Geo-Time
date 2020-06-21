import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {SERVER_API_URL} from '../app.constants';
import {createRequestOption, ClassUnit} from '../shared';

@Injectable()
export class TimetableService {

    private resourceUrl =  SERVER_API_URL + '/api/timetable';

    constructor(private http: HttpClient) { }

    query(req?: any): Observable<HttpResponse<ClassUnit[]>> {
        const options = createRequestOption(req);
        return this.http.get<ClassUnit[]>(this.resourceUrl + '/classes', { params: options, observe: 'response' })
            .map((res: HttpResponse<ClassUnit[]>) => this.convertClassUnitArrayResponse(res));
    }

    private convertClassUnitArrayResponse(res: HttpResponse<ClassUnit[]>): HttpResponse<ClassUnit[]> {
        const jsonResponse: ClassUnit[] = res.body;
        const body: ClassUnit[] = [];
        for (let i = 0; i < jsonResponse.length; i++) {
            body.push(this.convertClassUnitItemFromServer(jsonResponse[i]));
        }
        return res.clone({body});
    }

    private convertClassUnitItemFromServer(classUnit: ClassUnit): ClassUnit {
        return Object.assign({}, classUnit);
    }
}
