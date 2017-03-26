package com.italankin.lazyworker.app.handlers

import com.italankin.lazyworker.app.activity.ActivityManager
import com.italankin.lazyworker.app.core.Request

class TodayHandler extends DateHandler {

    TodayHandler(ActivityManager activityManager) {
        super(activityManager)
    }

    @Override
    String name() {
        return "today"
    }

    @Override
    protected long[] getInterval(Request request) {
        return intervalWithOffset(new Date(), 1)
    }

    @Override
    String helpMessage() {
        return "Usage: /today"
    }

}
