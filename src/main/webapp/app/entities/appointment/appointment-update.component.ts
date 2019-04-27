import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { JhiAlertService } from 'ng-jhipster';
import { IAppointment } from 'app/shared/model/appointment.model';
import { AppointmentService } from './appointment.service';
import { IRequest } from 'app/shared/model/request.model';
import { RequestService } from 'app/entities/request';

@Component({
    selector: 'jhi-appointment-update',
    templateUrl: './appointment-update.component.html'
})
export class AppointmentUpdateComponent implements OnInit {
    appointment: IAppointment;
    isSaving: boolean;

    requests: IRequest[];
    dateAndHour: string;

    constructor(
        protected jhiAlertService: JhiAlertService,
        protected appointmentService: AppointmentService,
        protected requestService: RequestService,
        protected activatedRoute: ActivatedRoute
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ appointment }) => {
            this.appointment = appointment;
            this.dateAndHour = this.appointment.dateAndHour != null ? this.appointment.dateAndHour.format(DATE_TIME_FORMAT) : null;
        });
        this.requestService
            .query({ 'appointmentId.specified': 'false' })
            .pipe(
                filter((mayBeOk: HttpResponse<IRequest[]>) => mayBeOk.ok),
                map((response: HttpResponse<IRequest[]>) => response.body)
            )
            .subscribe(
                (res: IRequest[]) => {
                    if (!this.appointment.requestId) {
                        this.requests = res;
                    } else {
                        this.requestService
                            .find(this.appointment.requestId)
                            .pipe(
                                filter((subResMayBeOk: HttpResponse<IRequest>) => subResMayBeOk.ok),
                                map((subResponse: HttpResponse<IRequest>) => subResponse.body)
                            )
                            .subscribe(
                                (subRes: IRequest) => (this.requests = [subRes].concat(res)),
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
        this.appointment.dateAndHour = this.dateAndHour != null ? moment(this.dateAndHour, DATE_TIME_FORMAT) : null;
        if (this.appointment.id !== undefined) {
            this.subscribeToSaveResponse(this.appointmentService.update(this.appointment));
        } else {
            this.subscribeToSaveResponse(this.appointmentService.create(this.appointment));
        }
    }

    protected subscribeToSaveResponse(result: Observable<HttpResponse<IAppointment>>) {
        result.subscribe((res: HttpResponse<IAppointment>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
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

    trackRequestById(index: number, item: IRequest) {
        return item.id;
    }
}
