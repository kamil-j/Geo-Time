import {SemesterHalf, WeekType} from '../shared';

export class TimetableView {
    constructor(
        public semesterHalf: SemesterHalf,
        public weekType: WeekType
    ) { }

    public hasNext() {
        return !(this.semesterHalf === SemesterHalf.HALF2 && this.weekType === WeekType.B);
    }

    public hasPrev() {
        return !(this.semesterHalf === SemesterHalf.HALF1 && this.weekType === WeekType.A);
    }

    public getNext() {
        if (this.semesterHalf === SemesterHalf.HALF1 && this.weekType === WeekType.A) {
            return new TimetableView(SemesterHalf.HALF1, WeekType.B);
        } else if (this.semesterHalf === SemesterHalf.HALF1 && this.weekType === WeekType.B) {
            return new TimetableView(SemesterHalf.HALF2, WeekType.A);
        } else if (this.semesterHalf === SemesterHalf.HALF2 && this.weekType === WeekType.A) {
            return new TimetableView(SemesterHalf.HALF2, WeekType.B);
        }
    }

    public getPrev() {
        if (this.semesterHalf === SemesterHalf.HALF2 && this.weekType === WeekType.B) {
            return new TimetableView(SemesterHalf.HALF2, WeekType.A);
        } else if (this.semesterHalf === SemesterHalf.HALF2 && this.weekType === WeekType.A) {
            return new TimetableView(SemesterHalf.HALF1, WeekType.B);
        } else if (this.semesterHalf === SemesterHalf.HALF1 && this.weekType === WeekType.B) {
            return new TimetableView(SemesterHalf.HALF1, WeekType.A);
        }
    }
}
