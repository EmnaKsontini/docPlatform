import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';

import { ApiAiClient } from 'api-ai-javascript/es6/ApiAiClient';
import { Observable } from 'rxjs/Observable';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { RequestService } from 'app/entities/request/request.service';
import { DoctorService } from 'app/entities/doctor/doctor.service';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Doctor, IDoctor } from 'app/shared/model/doctor.model';
import { IRequest, Request } from 'app/shared/model/request.model';
import { Moment } from 'moment';
import moment = require('moment');
import { User } from 'app/core';

// Message class for displaying messages in the component
export class Message {
    constructor(public content: string, public sentBy: string) {}
}

@Injectable()
export class ChatService {
    readonly token = environment.dialogflow.angularBot;
    readonly client = new ApiAiClient({ accessToken: this.token });

    conversation = new BehaviorSubject<Message[]>([]);

    information: object[];
    lock: boolean = true;
    chatBotRequest: IRequest;
    dateApp: Moment;

    constructor(protected requestService: RequestService, protected doctorService: DoctorService) {}

    // Sends and receives messages via DialogFlow
    converse(msg: string) {
        const userMessage = new Message(msg, 'user');
        this.update(userMessage);
        return this.client.textRequest(msg).then(res => {
            const speech = res.result.fulfillment.speech;
            if (res.result.parameters.date != undefined && res.result.parameters.date != '' && res.result.parameters.doctor != '') {
                this.dateApp = moment(res.result.parameters.date);
                this.doctorService.getDoctorByName(res.result.parameters.doctor).subscribe(
                    (res: HttpResponse<Doctor>) => {
                        var idDoctor = res.body.id;
                        this.doctorService.getCurrentUser().subscribe((res: HttpResponse<User>) => {
                            this.chatBotRequest = new Request(null, this.dateApp, null, null, false, res.body.id, idDoctor);
                            this.requestService.create(this.chatBotRequest).subscribe();
                        });
                    },
                    (res: HttpErrorResponse) => console.log('error')
                );
            }
            const botMessage = new Message(speech, 'bot');
            this.update(botMessage);
        });
    }

    // Adds message to source
    update(msg: Message) {
        this.conversation.next([msg]);
    }
}
