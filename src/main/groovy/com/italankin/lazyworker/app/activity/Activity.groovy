package com.italankin.lazyworker.app.activity

import com.italankin.lazyworker.app.utils.DateUtils

class Activity {

    int id
    int userId
    long startTime
    long finishTime
    String name
    String comment

    long duration() {
        if (!isCurrent()) {
            return finishTime - startTime
        }
        return DateUtils.currentTime() - startTime
    }

    String desc() {
        return "`$id` *$name*"
    }

    String detail() {
        StringBuilder sb = new StringBuilder()
        sb.append(desc())
        sb.append("\n*Started at*: _")
        sb.append(DateUtils.DATE_FORMAT_DETAIL.format(new Date(startTime)))
        if (!isCurrent()) {
            sb.append("_\n*Finished at*: _")
            sb.append(DateUtils.DATE_FORMAT_DETAIL.format(new Date(finishTime)))
        }
        sb.append("_\n*Session time*: _")
        sb.append(DateUtils.pretty(duration()))
        sb.append("_")
        if (comment != null) {
            sb.append("\n*Comment*: ")
            sb.append(comment)
        }
        return sb.toString()
    }

    boolean isCurrent() {
        return finishTime <= 0
    }

}
