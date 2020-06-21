import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {SERVER_API_URL} from '../../app.constants';
import {createRequestOption, Room} from '../../shared';

export type EntityResponseType = HttpResponse<Room>;

@Injectable()
export class RoomService {

    private resourceUrl =  SERVER_API_URL + 'api/rooms';

    constructor(private http: HttpClient) { }

    create(room: Room): Observable<EntityResponseType> {
        const copy = this.convert(room);
        return this.http.post<Room>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    update(room: Room): Observable<EntityResponseType> {
        const copy = this.convert(room);
        return this.http.put<Room>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<Room>(`${this.resourceUrl}/${id}`, { observe: 'response'})
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    query(req?: any): Observable<HttpResponse<Room[]>> {
        const options = createRequestOption(req);
        return this.http.get<Room[]>(this.resourceUrl, { params: options, observe: 'response' })
            .map((res: HttpResponse<Room[]>) => this.convertArrayResponse(res));
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response'});
    }

    private convertResponse(res: EntityResponseType): EntityResponseType {
        const body: Room = this.convertItemFromServer(res.body);
        return res.clone({body});
    }

    private convertArrayResponse(res: HttpResponse<Room[]>): HttpResponse<Room[]> {
        const jsonResponse: Room[] = res.body;
        const body: Room[] = [];
        for (let i = 0; i < jsonResponse.length; i++) {
            body.push(this.convertItemFromServer(jsonResponse[i]));
        }
        return res.clone({body});
    }

    /**
     * Convert a returned JSON object to Room.
     */
    private convertItemFromServer(room: Room): Room {
        return Object.assign({}, room);
    }

    /**
     * Convert a Room to a JSON which can be sent to the server.
     */
    private convert(room: Room): Room {
        return Object.assign({}, room);
    }
}
