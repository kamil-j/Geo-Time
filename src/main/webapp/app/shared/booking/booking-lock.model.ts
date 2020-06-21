export class BookingLock {
    constructor(
        public classUnitId?: number,
        public userId?: number,
        public lock?: boolean,
    ) {
    }
}
