package com.italankin.lazyworker.app.handlers

import com.italankin.lazyworker.app.activity.Activity
import com.italankin.lazyworker.app.activity.ActivityManager
import com.italankin.lazyworker.app.core.Handler
import com.italankin.lazyworker.app.utils.DateUtils
import com.italankin.lazyworker.app.utils.StringUtils
import io.fouad.jtb.core.TelegramBotApi
import io.fouad.jtb.core.beans.ReplyMarkup
import io.fouad.jtb.core.builders.ApiBuilder
import io.fouad.jtb.core.builders.InlineKeyboardButtonBuilder
import io.fouad.jtb.core.builders.ReplyMarkupBuilder
import io.fouad.jtb.core.enums.ParseMode

abstract class AbstractFinishHandler implements Handler {

    protected final ActivityManager activityManager

    AbstractFinishHandler(ActivityManager activityManager) {
        this.activityManager = activityManager
    }

    boolean finishCurrentActivity(String args, int senderId, TelegramBotApi api) {
        if (args) {
            Activity currentActivity = activityManager.getCurrentActivity(senderId)
            if (currentActivity?.id != args.toInteger()) {
                return false
            }
        }
        long finishTime = DateUtils.currentTime()
        Activity activity = activityManager.finishCurrentActivity(senderId, finishTime)
        if (activity) {
            String msg = String.format("Activity %s finished at _%s_.\nSession time: _%s_",
                    activity.desc(),
                    DateUtils.detail(finishTime),
                    DateUtils.pretty(activity.duration()))
            if (activity.comment != null && !activity.comment.isEmpty()) {
                msg = msg + "\n" + StringUtils.escapeMarkdown(activity.comment)
            }
            ReplyMarkup markup = ReplyMarkupBuilder.attachInlineKeyboard(
                    InlineKeyboardButtonBuilder.newRow()
                            .newButton("Show")
                            .withCallbackData("/show ${activity.id}")
                            .newButton("Resume")
                            .withCallbackData("/resume ${activity.id}")
                            .build())
                    .toReplyMarkup()
            return ApiBuilder.api(api)
                    .sendMessage(msg)
                    .toChatId(senderId)
                    .applyReplyMarkup(markup)
                    .parseMessageAs(ParseMode.MARKDOWN)
                    .execute()
        } else {
            return false
        }
    }

}
