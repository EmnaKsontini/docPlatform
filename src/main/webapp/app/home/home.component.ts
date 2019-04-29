import { Component, OnInit } from '@angular/core';
import { NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { LoginModalService, AccountService, Account } from 'app/core';
import { ITip } from 'app/shared/model/tip.model';
import { HomeService } from 'app/home/home.service';
import { IAppointment } from 'app/shared/model/appointment.model';
import { IDoctor } from 'app/shared/model/doctor.model';

@Component({
    selector: 'jhi-home',
    templateUrl: './home.component.html',
    styleUrls: ['home.scss']
})
export class HomeComponent implements OnInit {
    account: Account;
    modalRef: NgbModalRef;
    tips: ITip[];
    appointments: IAppointment[];
    MyDoctorsAppointment: IDoctor[];
    Mydoctors: IDoctor[];

    constructor(
        private homeService: HomeService,
        private accountService: AccountService,
        private loginModalService: LoginModalService,
        private eventManager: JhiEventManager
    ) {}

    ngOnInit() {
        this.accountService.identity().then((account: Account) => {
            this.account = account;
        });
        this.registerAuthenticationSuccess();
        this.homeService.getAllTips().subscribe(Alltips => {
            this.tips = Alltips;
        });
        this.homeService.getAllAppointments().subscribe(appointments => {
            this.appointments = appointments;
        });
        this.homeService.getAllNames().subscribe(names => {
            this.MyDoctorsAppointment = names;
        });
        this.homeService.getMyDoctors().subscribe(AllDoctors => {
            this.Mydoctors = AllDoctors;
        });
    }

    registerAuthenticationSuccess() {
        this.eventManager.subscribe('authenticationSuccess', message => {
            this.accountService.identity().then(account => {
                this.account = account;
            });
        });
    }

    isAuthenticated() {
        return this.accountService.isAuthenticated();
    }

    login() {
        this.modalRef = this.loginModalService.open();
    }
}
