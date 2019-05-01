import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import * as moment from 'moment';
import { JhiAlertService } from 'ng-jhipster';
import { IRequest, Request } from 'app/shared/model/request.model';
import { RequestService } from './request.service';
import { IPatient } from 'app/shared/model/patient.model';
import { PatientService } from 'app/entities/patient';
import { IDoctor } from 'app/shared/model/doctor.model';
import { DoctorService } from 'app/entities/doctor';
import { IAppointment } from 'app/shared/model/appointment.model';
import { AppointmentService } from 'app/entities/appointment';
import { User } from 'app/core';

@Component({
    selector: 'jhi-request-update',
    templateUrl: './request-update.component.html'
})
export class RequestUpdateComponent implements OnInit {
    request: IRequest;
    request2: IRequest;
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
            .query()
            .pipe(
                filter((mayBeOk: HttpResponse<IAppointment[]>) => mayBeOk.ok),
                map((response: HttpResponse<IAppointment[]>) => response.body)
            )
            .subscribe((res: IAppointment[]) => (this.appointments = res), (res: HttpErrorResponse) => this.onError(res.message));
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
    saveAutomatic() {
        this.isSaving = true;

        this.requestService.getCurrentUser().subscribe((res: HttpResponse<User>) => {
            this.request2 = new Request(
                null,
                this.request.date1,
                this.request.date2,
                this.request.date3,
                false,
                res.body.id,
                this.request.doctorId
            );
            //this.requestService.create(this.request2).subscribe();
            this.subscribeToSaveResponse(this.requestService.create(this.request2));
        });
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
