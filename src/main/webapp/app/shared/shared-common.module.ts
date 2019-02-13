import { NgModule } from '@angular/core';

import { DoctorsPlatformSharedLibsModule, FindLanguageFromKeyPipe, JhiAlertComponent, JhiAlertErrorComponent } from './';

@NgModule({
    imports: [DoctorsPlatformSharedLibsModule],
    declarations: [FindLanguageFromKeyPipe, JhiAlertComponent, JhiAlertErrorComponent],
    exports: [DoctorsPlatformSharedLibsModule, FindLanguageFromKeyPipe, JhiAlertComponent, JhiAlertErrorComponent]
})
export class DoctorsPlatformSharedCommonModule {}
