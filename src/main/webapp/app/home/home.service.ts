import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, Subject } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { ITip, Tip } from 'app/shared/model/tip.model';
import { Appointment, IAppointment } from 'app/shared/model/appointment.model';
import { Doctor, IDoctor } from 'app/shared/model/doctor.model';

@Injectable({ providedIn: 'root' })
export class HomeService {
    public resourceUrl = SERVER_API_URL + 'api';

    constructor(protected http: HttpClient) {}

    getAllTips(): Observable<Tip[]> {
        return this.http.get<ITip[]>(SERVER_API_URL + 'api/tipsAll');
    }

    getAllAppointments(): Observable<Appointment[]> {
        return this.http.get<IAppointment[]>(SERVER_API_URL + 'api/user/MyAppointments');
    }
    getAllNames(): Observable<Doctor[]> {
        return this.http.get<Doctor[]>(SERVER_API_URL + 'api/user/MyAppointmentsDoctor');
    }
    getMyDoctors(): Observable<IDoctor[]> {
        return this.http.get<IDoctor[]>(SERVER_API_URL + 'api/user/MyDoctors');
    }
}
