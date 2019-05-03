import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { map } from 'rxjs/operators';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { IRequest } from 'app/shared/model/request.model';
import { Patient } from 'app/shared/model/patient.model';

type EntityResponseType = HttpResponse<IRequest>;
type EntityArrayResponseType = HttpResponse<IRequest[]>;

@Injectable({ providedIn: 'root' })
export class RequestService {
    public resourceUrl = SERVER_API_URL + 'api/requests';
    public resourceSearchUrl = SERVER_API_URL + 'api/_search/requests';

    constructor(protected http: HttpClient) {}
    getCurrentUser(): Observable<EntityResponseType> {
        return this.http.get<Patient>(SERVER_API_URL + 'api' + '/getCurrentUser', { observe: 'response' });
    }

    create(request: IRequest): Observable<EntityResponseType> {
        const copy = this.convertDateFromClient(request);
        return this.http
            .post<IRequest>(this.resourceUrl, copy, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    createBot(request: IRequest): Observable<EntityResponseType> {
        const copy = this.convertDateFromClient(request);
        return this.http
            .post<IRequest>(this.resourceUrl, copy, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    update(request: IRequest): Observable<EntityResponseType> {
        const copy = this.convertDateFromClient(request);
        return this.http
            .put<IRequest>(SERVER_API_URL + 'createBot', copy, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http
            .get<IRequest>(`${this.resourceUrl}/${id}`, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http
            .get<IRequest[]>(this.resourceUrl, { params: options, observe: 'response' })
            .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    search(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http
            .get<IRequest[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
            .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
    }

    protected convertDateFromClient(request: IRequest): IRequest {
        const copy: IRequest = Object.assign({}, request, {
            date1: request.date1 != null && request.date1.isValid() ? request.date1.format(DATE_FORMAT) : null,
            date2: request.date2 != null && request.date2.isValid() ? request.date2.format(DATE_FORMAT) : null,
            date3: request.date3 != null && request.date3.isValid() ? request.date3.format(DATE_FORMAT) : null
        });
        return copy;
    }

    protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
        if (res.body) {
            res.body.date1 = res.body.date1 != null ? moment(res.body.date1) : null;
            res.body.date2 = res.body.date2 != null ? moment(res.body.date2) : null;
            res.body.date3 = res.body.date3 != null ? moment(res.body.date3) : null;
        }
        return res;
    }

    protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
        if (res.body) {
            res.body.forEach((request: IRequest) => {
                request.date1 = request.date1 != null ? moment(request.date1) : null;
                request.date2 = request.date2 != null ? moment(request.date2) : null;
                request.date3 = request.date3 != null ? moment(request.date3) : null;
            });
        }
        return res;
    }
}
