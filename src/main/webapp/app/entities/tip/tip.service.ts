import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { ITip } from 'app/shared/model/tip.model';

type EntityResponseType = HttpResponse<ITip>;
type EntityArrayResponseType = HttpResponse<ITip[]>;

@Injectable({ providedIn: 'root' })
export class TipService {
    public resourceUrl = SERVER_API_URL + 'api/tips';
    public resourceSearchUrl = SERVER_API_URL + 'api/_search/tips';

    constructor(protected http: HttpClient) {}

    create(tip: ITip): Observable<EntityResponseType> {
        return this.http.post<ITip>(this.resourceUrl, tip, { observe: 'response' });
    }

    update(tip: ITip): Observable<EntityResponseType> {
        return this.http.put<ITip>(this.resourceUrl, tip, { observe: 'response' });
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<ITip>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<ITip[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    search(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<ITip[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
    }
}
