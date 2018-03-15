package com.italankin.lazyworker.app.handlers

import com.italankin.lazyworker.app.core.Handler
import com.italankin.lazyworker.app.core.Request
import com.italankin.lazyworker.app.utils.DateUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class TimestampHandler implements Handler {
    private static final Logger LOG = LoggerFactory.getLogger(TimestampHandler.class)

    @Override
    String name() {
        return "timestamp"
    }

    @Override
    boolean handle(Request request) throws Exception {
        String args = request.getRawArgs()
        if (!args) {
            return false
        }
        String[] parts = args.split("[;.,\\-: ]")
        int h, m = 0
        try {
            if (parts[0].length() == 4) {
                h = parts[0][0..1].toInteger()
                m = parts[0][2..3].toInteger()
            } else {
                h = parts[0].toInteger()
                if (parts.length >= 2) {
                    m = parts[1].toInteger()
                }
            }
        } catch (NumberFormatException e) {
            LOG.error("handle:", e)
            return false
        }
        if (h < 0 || h > 23 || m < 0 || m > 59) {
            return false
        }
        Calendar calendar = DateUtils.getZoneCalendar()
        calendar.set(Calendar.HOUR_OF_DAY, h)
        calendar.set(Calendar.MINUTE, m)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        long time = calendar.getTimeInMillis()
        return request.response("Unix timestamp for `${DateUtils.detail(time)}` is `${time}`")
    }

    @Override
    String helpMessage() {
        return "Usage: /timestamp time\n\n" +
                "_time_ - local time in format `hh:mm` (24h format) to be converted to Unix timestamp"
    }
}
