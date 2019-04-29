import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { Doctor, IDoctor } from 'app/shared/model/doctor.model';
import { Patient } from 'app/shared/model/patient.model';
import { User } from 'app/core';

type EntityResponseType = HttpResponse<IDoctor>;
type EntityArrayResponseType = HttpResponse<IDoctor[]>;

@Injectable({ providedIn: 'root' })
export class DoctorService {
    public resourceUrl = SERVER_API_URL + 'api/doctors';
    public resourceSearchUrl = SERVER_API_URL + 'api/_search/doctors';

    constructor(protected http: HttpClient) {}

    create(doctor: IDoctor): Observable<EntityResponseType> {
        return this.http.post<IDoctor>(this.resourceUrl, doctor, { observe: 'response' });
    }

    update(doctor: IDoctor): Observable<EntityResponseType> {
        return this.http.put<IDoctor>(this.resourceUrl, doctor, { observe: 'response' });
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<IDoctor>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<IDoctor[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    search(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<IDoctor[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
    }
    getDoctorByName(name: any): Observable<EntityResponseType> {
        return this.http.post<Doctor>(SERVER_API_URL + 'api' + '/doctorByName', name, { observe: 'response' });
    }

    getCurrentUser(): Observable<EntityResponseType> {
        return this.http.get<Patient>(SERVER_API_URL + 'api' + '/getCurrentUser', { observe: 'response' });
    }
}
