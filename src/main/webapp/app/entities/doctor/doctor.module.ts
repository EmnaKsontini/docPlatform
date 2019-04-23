import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { JhiLanguageService } from 'ng-jhipster';
import { JhiLanguageHelper } from 'app/core';
import { AgmCoreModule } from '@agm/core';

import { DoctorsPlatformSharedModule } from 'app/shared';
import {
    DoctorComponent,
    DoctorDetailComponent,
    DoctorUpdateComponent,
    DoctorDeletePopupComponent,
    DoctorDeleteDialogComponent,
    doctorRoute,
    doctorPopupRoute
} from './';
import { MyDoctorsComponent } from './my-doctors.component';

const ENTITY_STATES = [...doctorRoute, ...doctorPopupRoute];

@NgModule({
    imports: [
        DoctorsPlatformSharedModule,
        RouterModule.forChild(ENTITY_STATES),
        AgmCoreModule.forRoot({
            apiKey: 'AIzaSyB4iF9FiFPJ8x1wuaHWRsk5-tEeAl9gqeU'
        })
    ],
    declarations: [
        DoctorComponent,
        DoctorDetailComponent,
        DoctorUpdateComponent,
        DoctorDeleteDialogComponent,
        DoctorDeletePopupComponent,
        MyDoctorsComponent
    ],
    entryComponents: [DoctorComponent, DoctorUpdateComponent, DoctorDeleteDialogComponent, DoctorDeletePopupComponent],
    providers: [{ provide: JhiLanguageService, useClass: JhiLanguageService }],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class DoctorsPlatformDoctorModule {
    constructor(private languageService: JhiLanguageService, private languageHelper: JhiLanguageHelper) {
        this.languageHelper.language.subscribe((languageKey: string) => {
            if (languageKey !== undefined) {
                this.languageService.changeLanguage(languageKey);
            }
        });
    }
}
