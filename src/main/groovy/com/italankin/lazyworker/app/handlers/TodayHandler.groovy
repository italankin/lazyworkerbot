package com.italankin.lazyworker.app.handlers

import com.italankin.lazyworker.app.activity.ActivityManager
import com.italankin.lazyworker.app.core.Command

class TodayHandler extends DateHandler {

    TodayHandler(ActivityManager activityManager) {
        super(activityManager)
    }

    @Override
    String name() {
        return "today"
    }

    @Override
    protected long[] getInterval(Command command) {
        return intervalWithOffset(new Date(), 1)
    }

    @Override
    String helpMessage() {
        return "Usage: /today"
    }

}
