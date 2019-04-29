import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, Subject } from 'rxjs';
import * as moment from 'moment';
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { map } from 'rxjs/operators';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { Appointment, IAppointment } from 'app/shared/model/appointment.model';
import { Doctor } from 'app/shared/model/doctor.model';

type EntityResponseType = HttpResponse<IAppointment>;
type EntityArrayResponseType = HttpResponse<IAppointment[]>;

@Injectable({ providedIn: 'root' })
export class AppointmentService {
    public resourceUrl = SERVER_API_URL + 'api/appointments';
    public resourceSearchUrl = SERVER_API_URL + 'api/_search/appointments';
    public _subject = new Subject<object>();
    public event = this._subject.asObservable();

    constructor(protected http: HttpClient) {}

    create(appointment: IAppointment): Observable<EntityResponseType> {
        const copy = this.convertDateFromClient(appointment);
        return this.http
            .post<IAppointment>(this.resourceUrl, copy, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    update(appointment: IAppointment): Observable<EntityResponseType> {
        const copy = this.convertDateFromClient(appointment);
        return this.http
            .put<IAppointment>(this.resourceUrl, copy, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http
            .get<IAppointment>(`${this.resourceUrl}/${id}`, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http
            .get<IAppointment[]>(this.resourceUrl, { params: options, observe: 'response' })
            .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    search(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http
            .get<IAppointment[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
            .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
    }

    protected convertDateFromClient(appointment: IAppointment): IAppointment {
        const copy: IAppointment = Object.assign({}, appointment, {
            dateAndHour: appointment.dateAndHour != null && appointment.dateAndHour.isValid() ? appointment.dateAndHour.toJSON() : null
        });
        return copy;
    }

    protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
        if (res.body) {
            res.body.dateAndHour = res.body.dateAndHour != null ? moment(res.body.dateAndHour) : null;
        }
        return res;
    }

    protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
        if (res.body) {
            res.body.forEach((appointment: IAppointment) => {
                appointment.dateAndHour = appointment.dateAndHour != null ? moment(appointment.dateAndHour) : null;
            });
        }
        return res;
    }

    getAppointmentList(): Observable<Appointment[]> {
        return this.http.get<Appointment[]>(SERVER_API_URL + 'api/user/MyAppointments');
    }

    getAllDoctorNamesAppointments(): Observable<Doctor[]> {
        return this.http.get<Doctor[]>(SERVER_API_URL + 'api/user/MyAppointmentsDoctor');
    }
}
