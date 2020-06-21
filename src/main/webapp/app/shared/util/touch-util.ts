export class TouchUtil {
    static isTouchDevice() {
        return ('ontouchstart' in document.documentElement);
    }
}
