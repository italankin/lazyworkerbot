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
        return oneDayInterval(new Date())
    }

    @Override
    String helpMessage() {
        return null
    }

}
