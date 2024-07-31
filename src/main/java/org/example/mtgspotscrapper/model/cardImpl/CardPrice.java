package org.example.mtgspotscrapper.model.cardImpl;

public record CardPrice(double prevPrice, double actPrice) {
    Availability getAvailability() {
        boolean isActPriceUnknown = actPrice == 0 || actPrice == -1;
        boolean isPrevPriceUnknown = prevPrice == 0 || prevPrice == -1;

        if (isActPriceUnknown) {
            return isPrevPriceUnknown ? Availability.UNAVAILABLE_PREV_UNAVAILABLE
                    : Availability.AVAILABLE_PREV_AVAILABLE;
        } else {
            return isPrevPriceUnknown ? Availability.AVAILABLE_PREV_UNAVAILABLE
                    : Availability.AVAILABLE_PREV_AVAILABLE;
        }
    }
}
