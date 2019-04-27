import { Moment } from 'moment';

export interface IRequest {
    id?: number;
    date1?: Moment;
    date2?: Moment;
    date3?: Moment;
    confirmation?: boolean;
    patientId?: number;
    doctorId?: number;
    appointmentId?: number;
}

export class Request implements IRequest {
    constructor(
        public id?: number,
        public date1?: Moment,
        public date2?: Moment,
        public date3?: Moment,
        public confirmation?: boolean,
        public patientId?: number,
        public doctorId?: number,
        public appointmentId?: number
    ) {
        this.confirmation = this.confirmation || false;
    }
}
