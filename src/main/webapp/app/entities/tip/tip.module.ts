import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { JhiLanguageService } from 'ng-jhipster';
import { JhiLanguageHelper } from 'app/core';
import { BsDropdownModule } from 'ngx-bootstrap';

import { DoctorsPlatformSharedModule } from 'app/shared';
import {
    TipComponent,
    TipDetailComponent,
    TipUpdateComponent,
    TipDeletePopupComponent,
    TipDeleteDialogComponent,
    tipRoute,
    tipPopupRoute
} from './';

const ENTITY_STATES = [...tipRoute, ...tipPopupRoute];

@NgModule({
    imports: [DoctorsPlatformSharedModule, RouterModule.forChild(ENTITY_STATES), BsDropdownModule.forRoot()],

    declarations: [TipComponent, TipDetailComponent, TipUpdateComponent, TipDeleteDialogComponent, TipDeletePopupComponent],
    entryComponents: [TipComponent, TipUpdateComponent, TipDeleteDialogComponent, TipDeletePopupComponent],
    providers: [{ provide: JhiLanguageService, useClass: JhiLanguageService }],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class DoctorsPlatformTipModule {
    constructor(private languageService: JhiLanguageService, private languageHelper: JhiLanguageHelper) {
        this.languageHelper.language.subscribe((languageKey: string) => {
            if (languageKey !== undefined) {
                this.languageService.changeLanguage(languageKey);
            }
        });
    }
}
