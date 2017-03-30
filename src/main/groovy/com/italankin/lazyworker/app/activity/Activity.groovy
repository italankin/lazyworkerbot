package com.italankin.lazyworker.app.activity

import com.italankin.lazyworker.app.utils.DateUtils
import com.italankin.lazyworker.app.utils.StringUtils

class Activity {

    final int id
    final int userId
    final long startTime
    final long finishTime
    final String name
    final String comment

    Activity(int id, int userId, String name, long startTime, long finishTime, String comment) {
        this.id = id
        this.userId = userId
        this.startTime = startTime
        this.finishTime = finishTime
        this.name = name
        this.comment = comment
    }

    long duration() {
        if (!isCurrent()) {
            return finishTime - startTime
        }
        return DateUtils.currentTime() - startTime
    }

    String desc() {
        return "`$id` *${name.replaceAll("\\*", "\\*")}*"
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
            sb.append(StringUtils.escapeMarkdown(comment))
        }
        return sb.toString()
    }

    boolean isCurrent() {
        return finishTime <= 0
    }

    @Override
    String toString() {
        return "Activity{" +
                "id=" + id +
                ", userId=" + userId +
                ", startTime=" + startTime +
                ", finishTime=" + finishTime +
                ", name='" + name + '\'' +
                ", comment='" + comment + '\'' +
                '}'
    }

}
