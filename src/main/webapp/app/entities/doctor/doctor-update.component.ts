import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { IDoctor } from 'app/shared/model/doctor.model';
import { DoctorService } from './doctor.service';

@Component({
    selector: 'jhi-doctor-update',
    templateUrl: './doctor-update.component.html'
})
export class DoctorUpdateComponent implements OnInit {
    doctor: IDoctor;
    isSaving: boolean;

    constructor(protected doctorService: DoctorService, protected activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ doctor }) => {
            this.doctor = doctor;
        });
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        if (this.doctor.id !== undefined) {
            this.subscribeToSaveResponse(this.doctorService.update(this.doctor));
        } else {
            this.subscribeToSaveResponse(this.doctorService.create(this.doctor));
        }
    }

    protected subscribeToSaveResponse(result: Observable<HttpResponse<IDoctor>>) {
        result.subscribe((res: HttpResponse<IDoctor>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    protected onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    protected onSaveError() {
        this.isSaving = false;
    }
}
