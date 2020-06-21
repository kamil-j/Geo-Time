import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {SERVER_API_URL} from '../../app.constants';
import {createRequestOption, StudyField} from '../../shared';

export type EntityResponseType = HttpResponse<StudyField>;

@Injectable()
export class StudyFieldService {

    private resourceUrl =  SERVER_API_URL + 'api/study-fields';

    constructor(private http: HttpClient) { }

    create(studyField: StudyField): Observable<EntityResponseType> {
        const copy = this.convert(studyField);
        return this.http.post<StudyField>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    update(studyField: StudyField): Observable<EntityResponseType> {
        const copy = this.convert(studyField);
        return this.http.put<StudyField>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<StudyField>(`${this.resourceUrl}/${id}`, { observe: 'response'})
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    query(req?: any): Observable<HttpResponse<StudyField[]>> {
        const options = createRequestOption(req);
        return this.http.get<StudyField[]>(this.resourceUrl, { params: options, observe: 'response' })
            .map((res: HttpResponse<StudyField[]>) => this.convertArrayResponse(res));
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response'});
    }

    private convertResponse(res: EntityResponseType): EntityResponseType {
        const body: StudyField = this.convertItemFromServer(res.body);
        return res.clone({body});
    }

    private convertArrayResponse(res: HttpResponse<StudyField[]>): HttpResponse<StudyField[]> {
        const jsonResponse: StudyField[] = res.body;
        const body: StudyField[] = [];
        for (let i = 0; i < jsonResponse.length; i++) {
            body.push(this.convertItemFromServer(jsonResponse[i]));
        }
        return res.clone({body});
    }

    /**
     * Convert a returned JSON object to StudyField.
     */
    private convertItemFromServer(studyField: StudyField): StudyField {
        return Object.assign({}, studyField);
    }

    /**
     * Convert a StudyField to a JSON which can be sent to the server.
     */
    private convert(studyField: StudyField): StudyField {
        return Object.assign({}, studyField);
    }
}
