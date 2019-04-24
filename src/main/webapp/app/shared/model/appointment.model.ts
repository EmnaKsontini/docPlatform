import { Moment } from 'moment';

export interface IAppointment {
    id?: number;
    date?: Moment;
    requestAppointement?: string;
    requestId?: number;
}

export class Appointment implements IAppointment {
    constructor(public id?: number, public date?: Moment, public requestAppointement?: string, public requestId?: number) {}
}
