package com.italankin.lazyworker.app.handlers

import com.italankin.lazyworker.app.activity.Activity
import com.italankin.lazyworker.app.activity.ActivityManager
import com.italankin.lazyworker.app.core.Command
import com.italankin.lazyworker.app.core.Handler
import com.italankin.lazyworker.app.utils.DateUtils

class ShowActivityHandler implements Handler {

    private final ActivityManager activityManager

    ShowActivityHandler(ActivityManager activityManager) {
        this.activityManager = activityManager
    }

    @Override
    String name() {
        return "show"
    }

    @Override
    boolean handle(Command command) throws Exception {
        String rawArgs = command.getRawArgs()
        if (rawArgs == null || rawArgs.isEmpty()) {
            return false
        }
        int id
        try {
            id = Integer.parseInt(rawArgs)
        } catch (NumberFormatException e) {
            return false
        }
        Activity activity = activityManager.getActivity(command.getSenderId(), id)
        if (activity == null) {
            return command.reply("No activity found with id $id.")
        }
        StringBuilder sb = new StringBuilder()
        sb.append("*ID*: _")
        sb.append(activity.id)
        sb.append("_\n*Name*: _")
        sb.append(activity.name)
        sb.append("_\n*Started at*: _")
        sb.append(DateUtils.DATE_FORMAT_DETAIL.format(new Date(activity.startTime)))
        if (activity.finishTime > 0) {
            sb.append("_\n*Finished at*: _")
            sb.append(DateUtils.DATE_FORMAT_DETAIL.format(new Date(activity.finishTime)))
            sb.append("_\n*Session time*: _")
            sb.append(DateUtils.pretty(activity.duration()))
        } else {
            sb.append("_\n*Time spent*: _")
            sb.append(DateUtils.pretty(DateUtils.currentTime() - activity.startTime))
        }
        sb.append("_")
        if (activity.comment != null) {
            sb.append("\n*Comment*: ")
            sb.append(activity.comment)
        }
        return command.reply(sb.toString())
    }

    @Override
    String helpMessage() {
        return "Usage: /show activity\\_id"
    }

}
