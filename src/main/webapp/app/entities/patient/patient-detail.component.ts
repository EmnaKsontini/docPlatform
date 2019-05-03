import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { JhiDataUtils } from 'ng-jhipster';

import { IPatient } from 'app/shared/model/patient.model';

@Component({
    selector: 'jhi-patient-detail',
    templateUrl: './patient-detail.component.html'
})
export class PatientDetailComponent implements OnInit {
    patient: IPatient;

    constructor(protected dataUtils: JhiDataUtils, protected activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ patient }) => {
            this.patient = patient;
        });
    }

    byteSize(field) {
        return this.dataUtils.byteSize(field);
    }

    openFile(contentType, field) {
        return this.dataUtils.openFile(contentType, field);
    }
    previousState() {
        window.history.back();
    }
}
