import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { IPatient } from 'app/shared/model/patient.model';
import { PatientService } from './patient.service';

@Component({
    selector: 'jhi-patient-update',
    templateUrl: './patient-update.component.html'
})
export class PatientUpdateComponent implements OnInit {
    patient: IPatient;
    isSaving: boolean;

    constructor(protected patientService: PatientService, protected activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ patient }) => {
            this.patient = patient;
        });
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        if (this.patient.id !== undefined) {
            this.subscribeToSaveResponse(this.patientService.update(this.patient));
        } else {
            this.subscribeToSaveResponse(this.patientService.create(this.patient));
        }
    }

    protected subscribeToSaveResponse(result: Observable<HttpResponse<IPatient>>) {
        result.subscribe((res: HttpResponse<IPatient>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    protected onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    protected onSaveError() {
        this.isSaving = false;
    }
}
