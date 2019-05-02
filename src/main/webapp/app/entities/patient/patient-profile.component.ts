import { Component, ElementRef, OnInit } from '@angular/core';
import { IPatient } from 'app/shared/model/patient.model';
import { IDoctor } from 'app/shared/model/doctor.model';
import { JhiAlertService, JhiDataUtils } from 'ng-jhipster';
import { PatientService } from 'app/entities/patient/patient.service';
import { DoctorService } from 'app/entities/doctor';
import { ActivatedRoute } from '@angular/router';
import { filter, map } from 'rxjs/operators';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

@Component({
    selector: 'jhi-patient-profile',
    templateUrl: './patient-profile.component.html',
    styles: []
})
export class PatientProfileComponent implements OnInit {
    patient: IPatient;
    isSaving: boolean;

    doctors: IDoctor[];

    constructor(
        protected dataUtils: JhiDataUtils,
        protected jhiAlertService: JhiAlertService,
        protected patientService: PatientService,
        protected doctorService: DoctorService,
        protected elementRef: ElementRef,
        protected activatedRoute: ActivatedRoute
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ patient }) => {
            this.patient = patient;
        });
        this.doctorService
            .query()
            .pipe(
                filter((mayBeOk: HttpResponse<IDoctor[]>) => mayBeOk.ok),
                map((response: HttpResponse<IDoctor[]>) => response.body)
            )
            .subscribe((res: IDoctor[]) => (this.doctors = res), (res: HttpErrorResponse) => this.onError(res.message));
    }

    byteSize(field) {
        return this.dataUtils.byteSize(field);
    }

    openFile(contentType, field) {
        return this.dataUtils.openFile(contentType, field);
    }

    setFileData(event, entity, field, isImage) {
        this.dataUtils.setFileData(event, entity, field, isImage);
    }

    clearInputImage(field: string, fieldContentType: string, idInput: string) {
        this.dataUtils.clearInputImage(this.patient, this.elementRef, field, fieldContentType, idInput);
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

    protected onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }

    trackDoctorById(index: number, item: IDoctor) {
        return item.id;
    }

    getSelected(selectedVals: Array<any>, option: any) {
        if (selectedVals) {
            for (let i = 0; i < selectedVals.length; i++) {
                if (option.id === selectedVals[i].id) {
                    return selectedVals[i];
                }
            }
        }
        return option;
    }
}
