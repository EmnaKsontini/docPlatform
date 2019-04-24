import { NgModule } from '@angular/core';

import { DoctorsPlatformSharedLibsModule, FindLanguageFromKeyPipe, JhiAlertComponent, JhiAlertErrorComponent } from './';
import {
    TipComponent,
    TipDeleteDialogComponent,
    TipDeletePopupComponent,
    TipDetailComponent,
    tipPopupRoute,
    tipRoute,
    TipUpdateComponent
} from 'app/entities/tip';
import { DoctorsPlatformTipModule } from 'app/entities/tip/tip.module';

@NgModule({
    imports: [DoctorsPlatformSharedLibsModule],
    declarations: [FindLanguageFromKeyPipe, JhiAlertComponent, JhiAlertErrorComponent],
    exports: [DoctorsPlatformSharedLibsModule, FindLanguageFromKeyPipe, JhiAlertComponent]
})
export class DoctorsPlatformSharedCommonModule {}
