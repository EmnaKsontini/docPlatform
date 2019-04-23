import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, Subject } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { ITip, Tip } from 'app/shared/model/tip.model';

type EntityResponseType = HttpResponse<ITip>;
type EntityArrayResponseType = HttpResponse<ITip[]>;

@Injectable({ providedIn: 'root' })
export class HomeService {
    public resourceUrl = SERVER_API_URL + 'api';

    constructor(protected http: HttpClient) {}

    getAllTips(): Observable<Tip[]> {
        return this.http.get<ITip[]>(SERVER_API_URL + 'api/tipsAll');
    }
}
