import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import * as moment from 'moment';
import { JhiAlertService } from 'ng-jhipster';
import { IRequest } from 'app/shared/model/request.model';
import { RequestService } from './request.service';
import { IPatient } from 'app/shared/model/patient.model';
import { PatientService } from 'app/entities/patient';
import { IDoctor } from 'app/shared/model/doctor.model';
import { DoctorService } from 'app/entities/doctor';
import { IAppointment } from 'app/shared/model/appointment.model';
import { AppointmentService } from 'app/entities/appointment';

@Component({
    selector: 'jhi-request-update',
    templateUrl: './request-update.component.html'
})
export class RequestUpdateComponent implements OnInit {
    request: IRequest;
    isSaving: boolean;

    patients: IPatient[];

    doctors: IDoctor[];

    appointments: IAppointment[];
    date1Dp: any;
    date2Dp: any;
    date3Dp: any;

    constructor(
        protected jhiAlertService: JhiAlertService,
        protected requestService: RequestService,
        protected patientService: PatientService,
        protected doctorService: DoctorService,
        protected appointmentService: AppointmentService,
        protected activatedRoute: ActivatedRoute
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ request }) => {
            this.request = request;
        });
        this.patientService
            .query()
            .pipe(
                filter((mayBeOk: HttpResponse<IPatient[]>) => mayBeOk.ok),
                map((response: HttpResponse<IPatient[]>) => response.body)
            )
            .subscribe((res: IPatient[]) => (this.patients = res), (res: HttpErrorResponse) => this.onError(res.message));
        this.doctorService
            .query()
            .pipe(
                filter((mayBeOk: HttpResponse<IDoctor[]>) => mayBeOk.ok),
                map((response: HttpResponse<IDoctor[]>) => response.body)
            )
            .subscribe((res: IDoctor[]) => (this.doctors = res), (res: HttpErrorResponse) => this.onError(res.message));
        this.appointmentService
            .query({ 'requestId.specified': 'false' })
            .pipe(
                filter((mayBeOk: HttpResponse<IAppointment[]>) => mayBeOk.ok),
                map((response: HttpResponse<IAppointment[]>) => response.body)
            )
            .subscribe(
                (res: IAppointment[]) => {
                    if (!this.request.appointmentId) {
                        this.appointments = res;
                    } else {
                        this.appointmentService
                            .find(this.request.appointmentId)
                            .pipe(
                                filter((subResMayBeOk: HttpResponse<IAppointment>) => subResMayBeOk.ok),
                                map((subResponse: HttpResponse<IAppointment>) => subResponse.body)
                            )
                            .subscribe(
                                (subRes: IAppointment) => (this.appointments = [subRes].concat(res)),
                                (subRes: HttpErrorResponse) => this.onError(subRes.message)
                            );
                    }
                },
                (res: HttpErrorResponse) => this.onError(res.message)
            );
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        if (this.request.id !== undefined) {
            this.subscribeToSaveResponse(this.requestService.update(this.request));
        } else {
            this.subscribeToSaveResponse(this.requestService.create(this.request));
        }
    }

    protected subscribeToSaveResponse(result: Observable<HttpResponse<IRequest>>) {
        result.subscribe((res: HttpResponse<IRequest>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    protected onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    protected onSaveError() {
        this.isSaving = false;
    }

    protected onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }

    trackPatientById(index: number, item: IPatient) {
        return item.id;
    }

    trackDoctorById(index: number, item: IDoctor) {
        return item.id;
    }

    trackAppointmentById(index: number, item: IAppointment) {
        return item.id;
    }
}
