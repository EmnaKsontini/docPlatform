import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { JhiLanguageService } from 'ng-jhipster';
import { JhiLanguageHelper } from 'app/core';

import { DoctorsPlatformSharedModule } from 'app/shared';
import {
    AppointmentComponent,
    AppointmentDetailComponent,
    AppointmentUpdateComponent,
    AppointmentDeletePopupComponent,
    AppointmentDeleteDialogComponent,
    appointmentRoute,
    appointmentPopupRoute
} from './';

const ENTITY_STATES = [...appointmentRoute, ...appointmentPopupRoute];

@NgModule({
    imports: [DoctorsPlatformSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [
        AppointmentComponent,
        AppointmentDetailComponent,
        AppointmentUpdateComponent,
        AppointmentDeleteDialogComponent,
        AppointmentDeletePopupComponent
    ],
    entryComponents: [AppointmentComponent, AppointmentUpdateComponent, AppointmentDeleteDialogComponent, AppointmentDeletePopupComponent],
    providers: [{ provide: JhiLanguageService, useClass: JhiLanguageService }],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class DoctorsPlatformAppointmentModule {
    constructor(private languageService: JhiLanguageService, private languageHelper: JhiLanguageHelper) {
        this.languageHelper.language.subscribe((languageKey: string) => {
            if (languageKey !== undefined) {
                this.languageService.changeLanguage(languageKey);
            }
        });
    }
}
