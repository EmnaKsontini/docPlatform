import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { JhiLanguageService } from 'ng-jhipster';
import { JhiLanguageHelper } from 'app/core';

import { DoctorsPlatformSharedModule } from 'app/shared';
import {
    PatientComponent,
    PatientDetailComponent,
    PatientUpdateComponent,
    PatientDeletePopupComponent,
    PatientDeleteDialogComponent,
    patientRoute,
    patientPopupRoute
} from './';
import { PatientProfileComponent } from './patient-profile.component';

const ENTITY_STATES = [...patientRoute, ...patientPopupRoute];

@NgModule({
    imports: [DoctorsPlatformSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [
        PatientComponent,
        PatientDetailComponent,
        PatientUpdateComponent,
        PatientDeleteDialogComponent,
        PatientDeletePopupComponent,
        PatientProfileComponent
    ],
    entryComponents: [PatientComponent, PatientUpdateComponent, PatientDeleteDialogComponent, PatientDeletePopupComponent],
    providers: [{ provide: JhiLanguageService, useClass: JhiLanguageService }],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class DoctorsPlatformPatientModule {
    constructor(private languageService: JhiLanguageService, private languageHelper: JhiLanguageHelper) {
        this.languageHelper.language.subscribe((languageKey: string) => {
            if (languageKey !== undefined) {
                this.languageService.changeLanguage(languageKey);
            }
        });
    }
}
